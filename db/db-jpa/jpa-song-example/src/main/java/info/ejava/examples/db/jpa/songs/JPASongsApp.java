package info.ejava.examples.db.jpa.songs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
//@org.springframework.data.jpa.repository.config.EnableJpaRepositories
//@EnableJpaRepositories(basePackageClasses = {SongsRepository.class}, repositoryImplementationPostfix = "Impl")
//@EnableJpaRepositories(basePackageClasses = {SongsRepository.class})
//@EntityScan(value={"info.ejava.examples.db.repo.jpa.songs.bo"})
//@EntityScan(basePackageClasses = {Song.class})
@Slf4j
public class JPASongsApp {
    public static void main(String ... args){
        SpringApplication.run(JPASongsApp.class, args);
    }

    @Component
    public class LogInfo implements CommandLineRunner {

        @Autowired
        private DataSource ds;
        @Autowired
        private EntityManagerFactory emf;
        @Autowired
        private EntityManager em;


        @Override
        public void run(String... args) throws Exception {
            log.info("dbUrl = {}", ds.getConnection().getMetaData().getURL());
            log.info("persistence unit = {}", emf);
            log.info("persistence context = {}", em);
        }

    }
}
