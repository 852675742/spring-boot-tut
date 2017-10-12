package dev.local.services;

import dev.local.domain.Project;
import dev.local.domain.TaskList;
import dev.local.domain.User;
import dev.local.repositories.ProjectRepository;
import dev.local.repositories.TaskListRepository;
import dev.local.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;

/**
 * Created by wangpeng on 2017/4/18.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository repository;
    private final UserRepository userRepository;
    private final TaskListRepository taskGroupRepository;

    @Autowired
    public ProjectServiceImpl(
            ProjectRepository repository,
            UserRepository userRepository,
            TaskListRepository taskGroupService){
        this.repository = repository;
        this.userRepository = userRepository;
        this.taskGroupRepository = taskGroupService;
    }

    @Override
    public Project add(Project project, String userId) {
        final User user = userRepository.findOne(userId);
        project.setOwner(user);
        project.setMembers(asList(user));
        project.setEnabled(true);
        project.setArchived(false);
        TaskList plan = new TaskList();
        plan.setName("计划");
        plan.setOrder(0);
        TaskList inProgress = new TaskList();
        inProgress.setName("进行中");
        inProgress.setOrder(1);
        TaskList done = new TaskList();
        done.setName("已完成");
        done.setOrder(2);
        project.setGroups(asList(
                taskGroupRepository.insert(plan),
                taskGroupRepository.insert(inProgress),
                taskGroupRepository.insert(done)));
        return repository.insert(project);
    }

    @Override
    public Project delete(String id) {
        final Project project = repository.findOne(id);
        repository.delete(id);
        return project;
    }

    @Override
    public Page<Project> findRelated(String userId, boolean enabled, boolean archived, Pageable pageable) {
//        final User user =  userRepository.findOne(userId);
        return repository.findRelated(new ObjectId(userId), enabled, archived, pageable);
    }

    @Override
    public Project findById(String id) {
        return repository.findOne(id);
    }

    @Override
    public Project update(Project project) {
        repository.save(project);
        return project;
    }
}
