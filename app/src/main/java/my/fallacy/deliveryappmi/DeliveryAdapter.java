package my.fallacy.deliveryappmi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import my.fallacy.deliveryappmi.model.Delivery;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.Holder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Delivery> deliveries;
    private DeliveryAdapterListener deliveryAdapterListener;

    public DeliveryAdapter(Context context, ArrayList<Delivery> deliveries, DeliveryAdapterListener deliveryAdapterListener) {
        this.context = context;
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
        holder.tvDescription.setText("");
        holder.tvAddress.setText("");

        Delivery delivery = getItem(position);

        if (delivery != null) {
            holder.tvDescription.setText(delivery.getDescription());
            holder.tvAddress.setText(delivery.getLocation().getAddress());
            Glide.with(context)
                    .load(delivery.getImageUrl())
//                .transition(withCrossFade())
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_launcher_round))
                    .apply(RequestOptions.errorOf(R.mipmap.ic_launcher_round))
                    .into(holder.ivDelivery);
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvDescription)
        TextView tvDescription;
        @BindView(R.id.tvAddress)
        TextView tvAddress;
        @BindView(R.id.ivDelivery)
        ImageView ivDelivery;

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
