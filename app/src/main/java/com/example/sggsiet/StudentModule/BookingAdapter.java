package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sggsiet.R;
import com.google.android.material.card.MaterialCardView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.eventName.setText("Event: " + booking.getEventName());
        holder.eventDate.setText(booking.getEventDate());
        holder.eventTime.setText(booking.getEventTime());
        holder.seatNo.setText("Seat No: " + booking.getSeatNo());
        holder.studentName.setText(booking.getStudentName());
        holder.studentEmail.setText(booking.getStudentEmail());
        holder.studentMobile.setText(booking.getStudentMobile());

        // Generate PDF when clicking "Download Ticket"
        holder.downloadPDF.setOnClickListener(v -> generatePDF(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventTime, seatNo, studentName, studentEmail, studentMobile, downloadPDF;
        MaterialCardView bookingCard;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.tvEventName);
            eventDate = itemView.findViewById(R.id.tvEventDate);
            eventTime = itemView.findViewById(R.id.tvEventTime);
            seatNo = itemView.findViewById(R.id.tvSeatNo);
            studentName = itemView.findViewById(R.id.tvStudentName);
            studentEmail = itemView.findViewById(R.id.tvStudentEmail);
            studentMobile = itemView.findViewById(R.id.tvStudentMobile);
            downloadPDF = itemView.findViewById(R.id.tvDownloadPDF);
            bookingCard = itemView.findViewById(R.id.bookingCard);
        }
    }



    private void generatePDF(Booking booking) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 850, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // 🎨 Paint object for styling
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        // 🏫 College Logo - Load from drawable
        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 100, 100, false);
        canvas.drawBitmap(scaledLogo, 250, 30, null);

        // 📜 College Name Header
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLUE);
        paint.setTextSize(22);
        canvas.drawText("Government College of Engineering", 300, 150, paint);

        paint.setTextSize(18);
        paint.setColor(Color.BLACK);
        canvas.drawText("Chhatrapati Sambhajinagar", 300, 175, paint);

        // 🏷️ Section Heading
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(20);
        canvas.drawText("Booking Ticket", 30, 220, paint);

        // 📌 Draw a line under heading
        paint.setColor(Color.BLACK);
        canvas.drawLine(30, 230, 570, 230, paint);

        // 📝 Event Details
        paint.setTextSize(16);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(Color.BLACK);
        canvas.drawText("Event: " + booking.getEventName(), 30, 260, paint);
        canvas.drawText("Date: " + booking.getEventDate(), 30, 290, paint);
        canvas.drawText("Time: " + booking.getEventTime(), 30, 320, paint);
        canvas.drawText("Seat No: " + booking.getSeatNo(), 30, 350, paint);

        // 🎟️ Student Details
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(18);
        paint.setColor(Color.BLUE);
        canvas.drawText("Student Details", 30, 400, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        paint.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Name: " + booking.getStudentName(), 30, 430, paint);
        canvas.drawText("Email: " + booking.getStudentEmail(), 30, 460, paint);
        canvas.drawText("Mobile: " + booking.getStudentMobile(), 30, 490, paint);

        // 📌 Footer Message
        paint.setTextSize(14);
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Thank you for your booking!", 300, 550, paint);

        document.finishPage(page);

        // 🗄️ Save PDF to Downloads Folder
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Booking_Ticket.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF Saved to Downloads", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();

        // 📂 Open PDF using FileProvider
        Uri uri = FileProvider.getUriForFile(context, "com.example.sggsiet.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }



}
