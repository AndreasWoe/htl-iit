package at.htlwels.btlescanner;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<BluetoothDevice> deviceList;
    private OnItemClickListener listener;

    // Constructor
    public MyAdapter(List<BluetoothDevice> deviceList, OnItemClickListener listener) {
        this.deviceList = deviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bt_item, parent, false);
        return new MyViewHolder(view, deviceList, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            String name, address;

            if(deviceList.get(position).getName() == null) {
                name = "Unknown Device";
            } else {
                name = deviceList.get(position).getName().toString();
            }

            address = deviceList.get(position).getAddress().toString();
            holder.deviceName.setText(name + " - " + address);
        } catch (SecurityException e) {
            //TODO: handle exception
        }

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    // ViewHolder class
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;

        public MyViewHolder(@NonNull View itemView, List<BluetoothDevice> deviceList, OnItemClickListener listener) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.txtBTItem);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(deviceList.get(position));
                }
            });
        }
    }
}
