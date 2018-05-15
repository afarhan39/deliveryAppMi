package my.fallacy.deliveryappmi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.Holder> {

    private ArrayList<Delivery> deliveries;
    private LayoutInflater inflater;
    private DeliveryAdapterListener deliveryAdapterListener;

    public DeliveryAdapter(Context context, ArrayList<Delivery> deliveries, DeliveryAdapterListener deliveryAdapterListener) {
        inflater = LayoutInflater.from(context);
        this.deliveries = deliveries;
        this.deliveryAdapterListener = deliveryAdapterListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.view_deliveryitem, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.tvName.setText("");
        holder.tvUrl.setText("");

        Delivery delivery = getItem(position);

        if (delivery != null) {
            holder.tvName.setText(delivery.getDescription());
            holder.tvUrl.setText(delivery.getLocation().getAddress());
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvUrl)
        TextView tvUrl;

        Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deliveryAdapterListener != null) {
                        Delivery delivery = getItem(getAdapterPosition());
                        if (delivery != null) {
                            deliveryAdapterListener.onCopyRightClicked(getAdapterPosition(), delivery);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    private Delivery getItem(int position) {
        if (position > -1 && position < deliveries.size())
            return deliveries.get(position);
        return null;
    }

    public interface DeliveryAdapterListener {
        void onCopyRightClicked(int position, Delivery delivery);
    }
}
