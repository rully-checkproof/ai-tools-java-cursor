package com.checkproof.explore.ai_tools_java_cursor.controller;

import com.checkproof.explore.ai_tools_java_cursor.dto.PaginatedResponseDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.PaginationRequestDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.TaskDto;
import com.checkproof.explore.ai_tools_java_cursor.dto.TaskStatisticsDto;
import com.checkproof.explore.ai_tools_java_cursor.model.Task;
import com.checkproof.explore.ai_tools_java_cursor.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getAllTasks_ShouldReturnPaginatedTasks() throws Exception {
        // Given
        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "Task 1"),
            createSampleTaskDto(2L, "Task 2")
        );

        PaginatedResponseDto<TaskDto> response = PaginatedResponseDto.<TaskDto>builder()
                .content(tasks)
                .build();

        when(taskService.getAllTasks(any(PaginationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/tasks")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Task 1"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Task 2"));

        verify(taskService).getAllTasks(any(PaginationRequestDto.class));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        // Given
        Long taskId = 1L;
        TaskDto task = createSampleTaskDto(taskId, "Specific Task");

        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(task));

        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Specific Task"));

        verify(taskService).getTaskById(taskId);
    }

    @Test
    void getTaskById_WhenTaskNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        Long taskId = 999L;

        when(taskService.getTaskById(taskId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(taskId);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        // Given
        TaskDto taskDto = createSampleTaskDto(null, "New Task");
        TaskDto createdTask = createSampleTaskDto(1L, "New Task");

        when(taskService.createTask(any(TaskDto.class))).thenReturn(createdTask);

        // When & Then
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"));

        verify(taskService).createTask(any(TaskDto.class));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        // Given
        Long taskId = 1L;
        TaskDto taskDto = createSampleTaskDto(taskId, "Updated Task");
        TaskDto updatedTask = createSampleTaskDto(taskId, "Updated Task");

        when(taskService.updateTask(eq(taskId), any(TaskDto.class))).thenReturn(updatedTask);

        // When & Then
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Updated Task"));

        verify(taskService).updateTask(eq(taskId), any(TaskDto.class));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        // Given
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);

        // When & Then
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(taskId);
    }

    @Test
    void getUpcomingTasks_ShouldReturnPaginatedTasks() throws Exception {
        // Given
        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "Upcoming Task 1"),
            createSampleTaskDto(2L, "Upcoming Task 2")
        );

        PaginatedResponseDto<TaskDto> response = PaginatedResponseDto.<TaskDto>builder()
                .content(tasks)
                .build();

        when(taskService.findUpcomingTasks(any(PaginationRequestDto.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/tasks/upcoming")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Upcoming Task 1"))
                .andExpect(jsonPath("$.content[1].title").value("Upcoming Task 2"));

        verify(taskService).findUpcomingTasks(any(PaginationRequestDto.class));
    }

    @Test
    void getTasksByDateRange_ShouldReturnPaginatedTasks() throws Exception {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "Range Task 1"),
            createSampleTaskDto(2L, "Range Task 2")
        );

        PaginatedResponseDto<TaskDto> response = PaginatedResponseDto.<TaskDto>builder()
                .content(tasks)
                .build();

        when(taskService.findTasksByDateRange(eq(start), eq(end), any(PaginationRequestDto.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/tasks/date-range")
                .param("start", start.toString())
                .param("end", end.toString())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Range Task 1"))
                .andExpect(jsonPath("$.content[1].title").value("Range Task 2"));

        verify(taskService).findTasksByDateRange(eq(start), eq(end), any(PaginationRequestDto.class));
    }

    @Test
    void searchTasksByTitle_ShouldReturnTasks() throws Exception {
        // Given
        String title = "project";
        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "Project Task 1"),
            createSampleTaskDto(2L, "Project Task 2")
        );

        when(taskService.searchTasksByTitle(title)).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/search")
                .param("title", title))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Project Task 1"))
                .andExpect(jsonPath("$[1].title").value("Project Task 2"));

        verify(taskService).searchTasksByTitle(title);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasks() throws Exception {
        // Given
        Task.TaskStatus status = Task.TaskStatus.IN_PROGRESS;
        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "In Progress Task 1"),
            createSampleTaskDto(2L, "In Progress Task 2")
        );

        when(taskService.findTasksByStatus(status)).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("In Progress Task 1"))
                .andExpect(jsonPath("$[1].title").value("In Progress Task 2"));

        verify(taskService).findTasksByStatus(status);
    }

    @Test
    void getTasksByPriority_ShouldReturnTasks() throws Exception {
        // Given
        Task.Priority priority = Task.Priority.HIGH;
        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "High Priority Task 1"),
            createSampleTaskDto(2L, "High Priority Task 2")
        );

        when(taskService.findTasksByPriority(priority)).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/priority/{priority}", priority))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("High Priority Task 1"))
                .andExpect(jsonPath("$[1].title").value("High Priority Task 2"));

        verify(taskService).findTasksByPriority(priority);
    }

    @Test
    void getOverdueTasks_ShouldReturnTasks() throws Exception {
        // Given
        List<TaskDto> tasks = Arrays.asList(
            createSampleTaskDto(1L, "Overdue Task 1"),
            createSampleTaskDto(2L, "Overdue Task 2")
        );

        when(taskService.findOverdueTasks()).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/api/tasks/overdue"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Overdue Task 1"))
                .andExpect(jsonPath("$[1].title").value("Overdue Task 2"));

        verify(taskService).findOverdueTasks();
    }

    @Test
    void getTaskStatistics_ShouldReturnStatistics() throws Exception {
        // Given
        TaskStatisticsDto statistics = TaskStatisticsDto.builder()
                .totalTasks(100L)
                .completedTasks(60L)
                .inProgressTasks(25L)
                .pendingTasks(10L)
                .overdueTasks(5L)
                .cancelledTasks(0L)
                .upcomingTasks(15L)
                .build();

        when(taskService.getTaskStatistics()).thenReturn(statistics);

        // When & Then
        mockMvc.perform(get("/api/tasks/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalTasks").value(100))
                .andExpect(jsonPath("$.completedTasks").value(60))
                .andExpect(jsonPath("$.inProgressTasks").value(25))
                .andExpect(jsonPath("$.pendingTasks").value(10))
                .andExpect(jsonPath("$.overdueTasks").value(5))
                .andExpect(jsonPath("$.cancelledTasks").value(0))
                .andExpect(jsonPath("$.upcomingTasks").value(15));

        verify(taskService).getTaskStatistics();
    }

    @Test
    void updateTaskStatus_ShouldReturnUpdatedTask() throws Exception {
        // Given
        Long taskId = 1L;
        Task.TaskStatus newStatus = Task.TaskStatus.COMPLETED;
        TaskDto updatedTask = createSampleTaskDto(taskId, "Completed Task");
        updatedTask.setStatus(newStatus);

        when(taskService.updateTaskStatus(taskId, newStatus)).thenReturn(updatedTask);

        // When & Then
        mockMvc.perform(patch("/api/tasks/{id}/status/{status}", taskId, newStatus))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value(newStatus.toString()));

        verify(taskService).updateTaskStatus(taskId, newStatus);
    }

    @Test
    void completeTask_ShouldReturnCompletedTask() throws Exception {
        // Given
        Long taskId = 1L;
        TaskDto completedTask = createSampleTaskDto(taskId, "Completed Task");
        completedTask.setStatus(Task.TaskStatus.COMPLETED);

        when(taskService.completeTask(taskId)).thenReturn(completedTask);

        // When & Then
        mockMvc.perform(patch("/api/tasks/{id}/complete", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value(Task.TaskStatus.COMPLETED.toString()));

        verify(taskService).completeTask(taskId);
    }

    @Test
    void startTask_ShouldReturnStartedTask() throws Exception {
        // Given
        Long taskId = 1L;
        TaskDto startedTask = createSampleTaskDto(taskId, "Started Task");
        startedTask.setStatus(Task.TaskStatus.IN_PROGRESS);

        when(taskService.startTask(taskId)).thenReturn(startedTask);

        // When & Then
        mockMvc.perform(patch("/api/tasks/{id}/start", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value(Task.TaskStatus.IN_PROGRESS.toString()));

        verify(taskService).startTask(taskId);
    }

    @Test
    void cancelTask_ShouldReturnCancelledTask() throws Exception {
        // Given
        Long taskId = 1L;
        TaskDto cancelledTask = createSampleTaskDto(taskId, "Cancelled Task");
        cancelledTask.setStatus(Task.TaskStatus.CANCELLED);

        when(taskService.cancelTask(taskId)).thenReturn(cancelledTask);

        // When & Then
        mockMvc.perform(patch("/api/tasks/{id}/cancel", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value(Task.TaskStatus.CANCELLED.toString()));

        verify(taskService).cancelTask(taskId);
    }

    @Test
    void putTaskOnHold_ShouldReturnOnHoldTask() throws Exception {
        // Given
        Long taskId = 1L;
        TaskDto onHoldTask = createSampleTaskDto(taskId, "On Hold Task");
        onHoldTask.setStatus(Task.TaskStatus.ON_HOLD);

        when(taskService.putTaskOnHold(taskId)).thenReturn(onHoldTask);

        // When & Then
        mockMvc.perform(patch("/api/tasks/{id}/hold", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.status").value(Task.TaskStatus.ON_HOLD.toString()));

        verify(taskService).putTaskOnHold(taskId);
    }

    // Helper methods
    private TaskDto createSampleTaskDto(Long id, String title) {
        return TaskDto.builder()
                .id(id)
                .title(title)
                .description("Sample task description")
                .startDate(LocalDateTime.of(2024, 12, 16, 10, 0))
                .endDate(LocalDateTime.of(2024, 12, 16, 11, 0))
                .priority(Task.Priority.MEDIUM)
                .status(Task.TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 