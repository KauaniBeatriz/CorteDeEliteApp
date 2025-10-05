package com.barbearia.cortedelite;

public class Agendamento {
    // Campos que salvamos no Firestore
    private String barbeiro;
    private String servico;
    private String data;
    private String horario;
    private String status;

    public Agendamento() {
        // Construtor vazio necessário para o Firebase
    }

    public Agendamento(String barbeiro, String servico, String data, String horario, String status) {
        this.barbeiro = barbeiro;
        this.servico = servico;
        this.data = data;
        this.horario = horario;
        this.status = status;
    }

    // Getters e Setters (Android Studio: Alt + Insert ou Command + N)
    public String getBarbeiro() { return barbeiro; }
    public void setBarbeiro(String barbeiro) { this.barbeiro = barbeiro; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

