package network;

import java.util.List;

import model.Employe;

public class EmployeResponse {
    private boolean success;
    private Data data;
    private String error;

    public static class Data {
        private List<model.Employe> Employe;

        public List<Employe> getEmploye() {
            return Employe;
        }

        public void setEmploye(List<Employe> Employe) {
            this.Employe = Employe;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
