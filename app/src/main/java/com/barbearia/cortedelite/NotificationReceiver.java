package com.barbearia.cortedelite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CorteDeElite_Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obter os dados do agendamento enviados pelo AlarmManager
        String barbeiro = intent.getStringExtra("BARBEIRO");
        String horario = intent.getStringExtra("HORARIO");

        // Disparar a notificação
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Configuração para Android 8.0 (Oreo) e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Agendamento",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Criar o conteúdo da notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Use um ícone real aqui
                .setContentTitle("⏰ Lembrete: Seu Corte de Elite!")
                .setContentText("Seu agendamento com " + barbeiro + " é às " + horario + ". Não se atrase!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true); // A notificação desaparece ao ser clicada

        // Enviar a notificação
        notificationManager.notify(0, builder.build());
    }
}
