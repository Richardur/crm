package network;

import java.util.List;

import model.Task;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TasksApi {


    @GET("tasks")
    Call<List<Task>> getAllTasks();
}
