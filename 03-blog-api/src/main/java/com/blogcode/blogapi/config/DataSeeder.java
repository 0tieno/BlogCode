package com.blogcode.blogapi.config;

import com.blogcode.blogapi.entity.Comment;
import com.blogcode.blogapi.entity.Post;
import com.blogcode.blogapi.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with a couple of sample posts and comments on startup, purely so a
 * student running this project for the first time immediately has data to explore with
 * {@code GET /api/v1/posts} instead of staring at an empty list.
 *
 * <p>Implementing {@link CommandLineRunner} is the standard Spring Boot mechanism for
 * running arbitrary code once, right after the application context has finished starting
 * but before it starts accepting traffic - a common home for this kind of one-off startup
 * task.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;

    /**
     * Inserts sample posts and comments only if the {@code posts} table is currently
     * empty, so restarting the application (or running it against a database that
     * already has real data) never duplicates or overwrites existing rows.
     *
     * @param args standard command-line arguments; unused here
     */
    @Override
    public void run(String... args) {
        if (postRepository.count() > 0) {
            return;
        }

        Post welcomePost = Post.builder()
                .title("Welcome to the Blog API")
                .content("This is a sample post created automatically on startup so you " +
                        "have something to query right away. Feel free to edit or delete it.")
                .author("System")
                .build();
        welcomePost.addComment(Comment.builder()
                .author("Ada")
                .content("Great first post!")
                .build());
        welcomePost.addComment(Comment.builder()
                .author("Grace")
                .content("Looking forward to more content.")
                .build());
        postRepository.save(welcomePost);

        Post secondPost = Post.builder()
                .title("Understanding JPA Relationships")
                .content("A short post explaining how @OneToMany and @ManyToOne work " +
                        "together to model a Post-to-Comments relationship.")
                .author("System")
                .build();
        secondPost.addComment(Comment.builder()
                .author("Linus")
                .content("The mappedBy attribute finally makes sense now, thanks!")
                .build());
        postRepository.save(secondPost);
    }
}
