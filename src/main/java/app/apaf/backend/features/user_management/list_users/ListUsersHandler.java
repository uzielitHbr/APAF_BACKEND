package app.apaf.backend.features.user_management.list_users;


import app.apaf.backend.domain.enums.UserStatus;
import app.apaf.backend.domain.users.User;
import app.apaf.backend.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * Service responsible for retrieving and filtering the system's user .
  Allows fetching the complete list of registered users or performing
 dynamic filtering based on their account status ({@link UserStatus})
  @Author Uziel Abraham
  @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ListUsersHandler {

    private final UserRepository userRepository;

    /**
     * Retrieves the processed user list ready to be consumed by the client.
     *
     * @param status Optional filter by user status. If {@code null}, absolutely all
     * records in the system will be fetched and returned.
     * @return A {@link ListUsersResult} containing the collection of secure user DTOs.
     */
    @Transactional(readOnly = true)
    public ListUsersResult getListUsersHandler(
            UserStatus status
    ) {

        List<User> listUsers ;
        if ( status == null ) {
            listUsers = userRepository.findAll();
        }else {

        listUsers = userRepository.findAllByStatus(status);
        }

        List<ListUsersQuery>  listUsersQuery = listUsers.stream()
                .map(user -> new ListUsersQuery(
                        user.getIdUser(),
                        user.getFullName(),
                        user.getPhoneNumber(),
                        user.getEmail(),
                        user.getRole().getCodeRole(),
                        user.getStatus()
                        )

                ).toList();

        return new ListUsersResult(listUsersQuery);

    }
}
