package ru.otus.mk.libraryapp.dao.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.mk.libraryapp.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GenreDaoImpl implements GenreDao {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final Map<Long, Genre> genreMap;


    @Autowired
    public GenreDaoImpl(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        genreMap = new HashMap<>();
        reloadGenres();
    }
    @Override
    public void insert(Genre genre) {
        final String sql = "insert into genres(genre_id, genre_name) values( default, :name)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", genre.getGenreName());

        KeyHolder kh = new GeneratedKeyHolder();

        namedParameterJdbcOperations.update(sql, params, kh);
        genre.setId( kh.getKey().longValue());

    }

    @Override
    public List<Genre> getAllGenreList() {
        final String sql = "select * from genres ";
        return namedParameterJdbcOperations.query(sql, new GenreRowMapper() );
    }

    @Override
    public Genre getByName(String genreName) {
        String sql = "select * from genres where upper(trim(genre_name))=:name";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", genreName.trim().toUpperCase());
        try {
            return namedParameterJdbcOperations.queryForObject(sql, params, new GenreRowMapper());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public Genre getByID(long id) {
        String sql = "select * from genres where genre_id=:id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return namedParameterJdbcOperations.queryForObject(sql, params, new GenreRowMapper() );
    }

    @Override
    public Genre getByCachedID(long id) {
        return genreMap.get(id);

    }

    @Override
    public void reloadGenres() {
        genreMap.putAll(getLinkedGenreList().stream().collect(Collectors.toMap(Genre::getId, x -> x)));
    }

    @Override
    public List<Genre> getLinkedGenreList() {
        final String sql = "select distinct a.* from genres a natural join genre_book";
        return namedParameterJdbcOperations.query(sql, new GenreRowMapper() );
    }

    public static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre(rs.getString("genre_name"));
            genre.setId(rs.getLong("genre_id"));
            return genre;
        }
    }
}
