/**
 * Created by user on 18/08/2017.
 */

package com.example.rober.gallery;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Food> foodsList;

    public FoodListAdapter(Context context, int layout, ArrayList<Food> foodsList) {
        this.context = context;
        this.layout = layout;
        this.foodsList = foodsList;
    }

    @Override
    public int getCount() {
        return foodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName,txtPrice;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row=view;
        ViewHolder holder=new ViewHolder();

        if(row == null){
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(layout,null);

            holder.txtName=(TextView)row.findViewById(R.id.txtName);
            holder.txtPrice=(TextView)row.findViewById(R.id.txtPrice);
            holder.imageView=(ImageView) row.findViewById(R.id.imgFood);
            row.setTag(holder);
        }else{
            holder=(ViewHolder)row.getTag();
        }
        Food food=foodsList.get(position);

        holder.txtName.setText(food.getName());
        holder.txtPrice.setText(food.getPrice());

        byte[] foodImage=food.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        //Picasso.with(context).load("http://192.168.137.175/try/client/uploads/model1.jpg").into(holder.imageView);
        return row;
    }
}
