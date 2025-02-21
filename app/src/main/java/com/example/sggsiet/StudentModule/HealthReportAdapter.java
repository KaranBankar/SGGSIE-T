package com.example.sggsiet.StudentModule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sggsiet.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HealthReportAdapter extends RecyclerView.Adapter<HealthReportAdapter.ViewHolder> {

    private final Context context;
    private final List<HealthReportModel> reportList;

    public HealthReportAdapter(Context context, List<HealthReportModel> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_health_report_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthReportModel report = reportList.get(position);

        if (report == null) {
            Toast.makeText(context, "Null report found!", Toast.LENGTH_SHORT).show();
            return;
        }

        holder.tvName.setText("Name: " + report.getName());
        holder.tvEmail.setText("Email: " + report.getEmail());
        holder.tvContact.setText("Contact: " + report.getStudentMobile());
        holder.tvReportDateTime.setText("Date: " + report.getCurrentdate() + " | Time: " + report.getCurrenttime());
        holder.tvHealthIssue.setText("Health Issue: " + report.getHealthissue());
        holder.tvDescription.setText("Description: " + report.getDescription());

        holder.btnDownloadReport.setOnClickListener(v -> generatePDF(report));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvContact, tvReportDateTime, tvHealthIssue, tvDescription;
        Button btnDownloadReport;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvContact = itemView.findViewById(R.id.tv_contact);
            tvReportDateTime = itemView.findViewById(R.id.tv_report_date_time);
            tvHealthIssue = itemView.findViewById(R.id.tv_health_issue);
            tvDescription = itemView.findViewById(R.id.tv_description);
            btnDownloadReport = itemView.findViewById(R.id.btn_download_report);
        }
    }

    private void generatePDF(HealthReportModel report) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 850, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 100, 100, false);
        canvas.drawBitmap(scaledLogo, 250, 30, null);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLUE);
        paint.setTextSize(22);
        canvas.drawText("Shri Guru Gobind Singhji Institute of Engineering", 300, 150, paint);

        paint.setTextSize(18);
        paint.setColor(Color.BLACK);
        canvas.drawText("and Technology (SGGSIET) Nanded", 300, 175, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(20);
        canvas.drawText("Health Report", 30, 220, paint);

        paint.setColor(Color.BLACK);
        canvas.drawLine(30, 230, 570, 230, paint);
        paint.setTextSize(16);
        paint.setTypeface(android.graphics.Typeface.DEFAULT);
        canvas.drawText("Name: " + report.getName(), 30, 260, paint);
        canvas.drawText("Email: " + report.getEmail(), 30, 290, paint);
        canvas.drawText("Contact: " + report.getStudentMobile(), 30, 320, paint);
        canvas.drawText("Date: " + report.getCurrentdate(), 30, 350, paint);
        canvas.drawText("Time: " + report.getCurrenttime(), 30, 380, paint);
        canvas.drawText("Department: " + report.getDepartment(), 30, 410, paint);
        canvas.drawText("Health Issue: " + report.getHealthissue(), 30, 440, paint);
        canvas.drawText("Description: " + report.getDescription(), 30, 470, paint);

        document.finishPage(page);

        File file;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "HealthReport.pdf");
        } else {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "HealthReport.pdf");
        }

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF Saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }

        document.close();

        Uri uri = FileProvider.getUriForFile(context, "com.example.sggsiet.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
