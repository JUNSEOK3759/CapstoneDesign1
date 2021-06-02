package com.work.cafe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.work.cafe.R;
import com.work.cafe.data.BookingData;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<BookingData> bookingDataList = new ArrayList<>();
    private OnClickBooking onClickBooking;

    public BookingAdapter(OnClickBooking onClickBooking) {
        this.onClickBooking = onClickBooking;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_booking, parent, false);
        BookingViewHolder bookingViewHolder = new BookingViewHolder(holder);
        bookingViewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = bookingViewHolder.getAdapterPosition();
                onClickBooking.onClickReject(bookingDataList.get(position), position);
            }
        });

        bookingViewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = bookingViewHolder.getAdapterPosition();
                onClickBooking.onClickAccept(bookingDataList.get(position), position);
            }
        });

        return bookingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        BookingViewHolder bookingViewHolder = (BookingViewHolder) holder;

        BookingData bookingData = bookingDataList.get(position);

        bookingViewHolder.name.setText(bookingData.nickname);
        bookingViewHolder.scheduled.setText("도착 예정 : " + bookingData.scheduled + " 분 후");
        bookingViewHolder.personnel.setText("예약 인원 : " + bookingData.personnel + " 명");
    }

    @Override
    public int getItemCount() {
        return bookingDataList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView name;
    TextView personnel;
    TextView scheduled;

    Button reject;
    Button accept;

    public BookingViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.nickname);
        personnel = itemView.findViewById(R.id.personnel);
        scheduled = itemView.findViewById(R.id.scheduled);

        reject = itemView.findViewById(R.id.reject);
        accept = itemView.findViewById(R.id.accept);
    }
}


    public interface OnClickBooking {
        void onClickReject(BookingData bookingData, int position);

        void onClickAccept(BookingData bookingData, int position);
    }

    public void addBookingData(BookingData bookingData) {
        bookingDataList.add(0, bookingData);
        notifyItemInserted(0);
    }

    public void removeBookingData(BookingData bookingData) {
        int removePosition = -1;
        for (int i = 0; i < bookingDataList.size(); i++) {
            if (bookingDataList.get(i).userEmail.equals(bookingData.userEmail)) {
                removePosition = i;
                break;
            }
        }

        bookingDataList.remove(removePosition);
        notifyItemRemoved(removePosition);
    }

    public void removeBookingDataByPosition(int position) {
        bookingDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void setBookingDataList(ArrayList<BookingData> bookingDataList) {
        this.bookingDataList = bookingDataList;
        notifyDataSetChanged();
    }
}
