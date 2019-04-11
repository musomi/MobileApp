package com.example.rober.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.rober.gallery.R.id.imageView;
import static com.example.rober.gallery.R.id.imageViewFood;

/**
 * Created by user on 18/08/2017.
 */

public class FoodList extends AppCompatActivity {
    GridView gridView;
    ArrayList<Food>list;
    FoodListAdapter adapter=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_activity);

        gridView=(GridView)findViewById(R.id.gridView);
        list=new ArrayList<>();
        adapter=new FoodListAdapter(this,R.layout.food_items,list);
        gridView.setAdapter(adapter);

        //get all data from sqlite
        final SQLiteHelper helper=new SQLiteHelper(getApplicationContext());
        final Cursor cursor=helper.getAllData();
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String name=cursor.getString(1);
            String price=cursor.getString(2);
            byte[] image=cursor.getBlob(3);

            list.add(new Food(id,name,price,image));

        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] items={"Update","Delete"};
                final AlertDialog.Builder dialog=new AlertDialog.Builder(FoodList.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int item){
                    if(item==0){
                        //update
                        //Toast.makeText(getApplicationContext(),"Update...",Toast.LENGTH_LONG).show();
                       Cursor cursor1= helper.getAllData();
                        ArrayList<Integer> arrID=new ArrayList<Integer>();
                        ArrayList<String> name=new ArrayList<String>();
                        ArrayList<String> price=new ArrayList<String>();

                        while (cursor1.moveToNext()){
                        arrID.add(cursor1.getInt(0));
                            name.add(cursor1.getString(1));
                            price.add(cursor1.getString(2));

                            //Toast.makeText(getApplicationContext(),cursor1.getInt(0),Toast.LENGTH_SHORT).show();
                        }
                        showDialogUpdate(FoodList.this,arrID.get(position),name.get(position),price.get(position));
                    }else{
                        //delete
                        //Toast.makeText(getApplicationContext(),"Delete...",Toast.LENGTH_LONG).show();

                        Cursor cursor1= helper.getCol();
                        ArrayList<Integer> arrID=new ArrayList<Integer>();
                        while (cursor1.moveToNext()){
                            arrID.add(cursor1.getInt(0));
                            //Toast.makeText(getApplicationContext(),cursor1.getInt(0),Toast.LENGTH_SHORT).show();
                        }

                        showDialogDelete(arrID.get(position));
                    }

                    }
                });
                dialog.show();
                return true;
            }
        });

    }
    ImageView imageViewFood;

    private void showDialogUpdate(Activity activity, final int position,String name,String price){
        final Dialog dialog=new Dialog(activity);
        dialog.setContentView(R.layout.update_food);
        dialog.setTitle("Update");

        imageViewFood=(ImageView)dialog.findViewById(R.id.imageViewFood);
        final EditText edtName=(EditText)dialog.findViewById(R.id.edtName);
        final EditText edtPrice=(EditText)dialog.findViewById(R.id.edtPrice);
        Button btnUpdate=(Button)dialog.findViewById(R.id.btnUpdate);

        edtName.setText(name);
        edtPrice.setText(price);

        //set width for dialog
        int width= (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height for dialog
        int height=(int) (activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        imageViewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request photo

                ActivityCompat.requestPermissions(
                        FoodList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        000
                );

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    SQLiteHelper helper=new SQLiteHelper(getApplicationContext());
                    helper.updateData(edtName.getText().toString().trim(),
                            edtPrice.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewFood),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Update successfull",Toast.LENGTH_SHORT).show();
                }catch (Exception error){
              Log.e("Update error:",error.getMessage());
                }
                updateFoodList();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 000){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,000);
            }else{
                Toast.makeText(getApplicationContext(),"You dont have permission to access file location!!",Toast.LENGTH_SHORT).show();
            }
            return;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 000 && resultCode == RESULT_OK && data !=null){
            Uri uri=data.getData();

            try {
                InputStream inputStream=getContentResolver().openInputStream(uri);

                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                imageViewFood.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialogDelete(final int idFood){
        AlertDialog.Builder dialogDelete=new AlertDialog.Builder(FoodList.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to delete this?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    MainActivity.sqLiteHelper.deleteData(idFood);
                    Toast.makeText(getApplicationContext(),"Deletion success!!",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("error",e.getMessage());
                }
                updateFoodList();

            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();

    }

    private void updateFoodList(){
        SQLiteHelper helper=new SQLiteHelper(getApplicationContext());
        Cursor cursor=helper.getAllData();
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String name=cursor.getString(1);
            String price=cursor.getString(2);
            byte[] image=cursor.getBlob(3);

            list.add(new Food(id,name,price,image));

        }
        adapter.notifyDataSetChanged();

    }
}
