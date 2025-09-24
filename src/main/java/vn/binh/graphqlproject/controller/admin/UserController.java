package vn.binh.graphqlproject.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.binh.graphqlproject.entity.User;
import vn.binh.graphqlproject.service.IUserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    @Autowired
    private IUserService userService;

    @QueryMapping
    public List<User> users() {
        return userService.findAll();
    }

    @QueryMapping
    public User userById(@Argument Long id) {
        return userService.findById(id).orElse(null);
    }

    @MutationMapping
    public User createUser(@Argument UserInput input) {
        User u = new User();
        u.setUserName(input.getUserName());
        u.setFullName(input.getFullName());
        u.setEmail(input.getEmail());
        u.setPassword(input.getPassword());
        u.setPhone(input.getPhone());
        return userService.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UserInput input) {
        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return null;
        User u = opt.get();
        u.setUserName(input.getUserName());
        u.setFullName(input.getFullName());
        u.setEmail(input.getEmail());
        u.setPassword(input.getPassword());
        u.setPhone(input.getPhone());
        return userService.save(u);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return false;
        userService.delete(opt.get());
        return true;
    }

    // ===== AJAX view pages for Users =====
    @GetMapping("ajax/list")
    public String listAjax() {
        return "admin/user/list-ajax";
    }

    @GetMapping("ajax/add")
    public String addAjax() {
        return "admin/user/add-ajax";
    }

    @GetMapping("ajax/update")
    public String updateAjax() {
        return "admin/user/update-ajax";
    }

    @GetMapping("ajax/delete")
    public String deleteAjax() {
        return "admin/user/delete-ajax";
    }

    // GraphQL input type mapping
    public static class UserInput {
        private String userName;
        private String fullName;
        private String email;
        private String password;
        private String phone;

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
