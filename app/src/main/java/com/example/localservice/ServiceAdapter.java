package com.example.localservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements Filterable {
    private List<Service> serviceList; // Displayed list
    private List<Service> serviceListFull; // Full list for filtering
    private final Context context;

    public ServiceAdapter(List<Service> serviceList, Context context) {
        this.serviceList = serviceList;
        this.context = context;
        this.serviceListFull = new ArrayList<>(serviceList); // Copy the list for filtering
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (service.getServiceId() == null || service.getServiceId().isEmpty()) {
                Toast.makeText(context, "Service ID is missing", Toast.LENGTH_SHORT).show();
                Log.e("ServiceAdapter", "Missing service ID for: " + service.getName());
                return;
            }

            if (context != null) {
                Intent intent = new Intent(context, ServiceDetailsActivity.class);
                intent.putExtra("service_id", service.getServiceId());
                Log.d("ServiceAdapter", "Opening ServiceDetailsActivity for ID: " + service.getServiceId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Service> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(serviceListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Service service : serviceListFull) {
                        if (service.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(service);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                serviceList.clear();
                if (results.values != null) {
                    serviceList.addAll((List<Service>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        final TextView serviceName;
        final TextView serviceDescription;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.service_name);
            serviceDescription = itemView.findViewById(R.id.service_description);
        }
    }
}
