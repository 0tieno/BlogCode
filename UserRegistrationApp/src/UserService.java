//public class UserService {
//
//    private final UserRepository repository;
//
//    public UserService(UserRepository repository) {
//        this.repository = repository;
//    }
//
//    public String registerUser(String username, String email) {
//
//        if (username == null || username.isEmpty()) {
//            return "Username is required";
//        }
//
//        if (email == null || email.isEmpty()) {
//            return "Email is required";
//        }
//
//        if (repository.emailExists(email)) {
//            return "Email already registered";
//        }
//
//        repository.save(new User(username, email));
//        return "User registered successfully";
//    }
//}
