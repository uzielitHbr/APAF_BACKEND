package app.apaf.backend.features.user_management.list_users;

import java.util.List;

public record ListUsersResult(
      List<ListUsersQuery>  listUsersQuery
) {
}
