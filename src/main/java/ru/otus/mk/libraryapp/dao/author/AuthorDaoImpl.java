package ru.otus.mk.libraryapp.dao.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.mk.libraryapp.domain.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AuthorDaoImpl implements AuthorDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Autowired
    public AuthorDaoImpl(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }



    @Override
    public void insert(final Author author) {
        final String sql = "insert into authors(author_id, first_name, last_name, middle_name) values( default, :first_name, :last_name, :middle_name)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("first_name", author.getFirstName());
        params.addValue("last_name", author.getLastName());
        params.addValue("middle_name", author.getMiddleName());

        KeyHolder kh = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update(sql, params, kh);
        author.setId( kh.getKey().longValue());

    }

    @Override
    public List<Author> getAllAuthorList() {
        final String sql = "select * from authors ";
        return namedParameterJdbcOperations.query(sql, new AuthorRowMapper() );
    }

    @Override
    public List<Author> getByFIO(String lastName, String firstName, String middleName) {
        String sql = "select * from authors where upper(trim(last_name))=:last_name and upper(trim(first_name))=:first_name";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("last_name", lastName.trim().toUpperCase());
        params.addValue("first_name", firstName.trim().toUpperCase());
        if(middleName == null || middleName.isEmpty()) {
            sql = sql + " and upper(trim(middle_name))=:middle_name";
            params.addValue("middle_name", middleName.trim().toUpperCase());
        }
        return namedParameterJdbcOperations.query(sql, params, new AuthorRowMapper() );
    }

    @Override
    public Author getByID(long id) {
        final String sql = "select * from authors where author_id=:id";
        Map<String, Object> params = Collections.singletonMap("id",id);
        return namedParameterJdbcOperations.queryForObject(sql, params, new AuthorRowMapper());
    }

    @Override
    public Map<Long, Author> getLinkedAuthorMap() {
        final String sql = "select distinct a.* from authors a natural join author_book";
        return namedParameterJdbcOperations.query(sql, rs -> {
            Map<Long, Author> authorMap = new HashMap<>();
            while(rs.next()) {
                authorMap.put(rs.getLong("author_id"), new Author(rs.getLong("author_id")
                        , rs.getString("last_name")
                        , rs.getString("first_name")
                        , rs.getString("middle_name")));
            }
            return authorMap;
        } );
    }

    public static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Author(rs.getLong("author_id")
                    , rs.getString("last_name")
                    , rs.getString("first_name")
                    , rs.getString("middle_name"));
        }
    }
}
