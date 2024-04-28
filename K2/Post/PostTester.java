package K2.Post;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Comment {
    private final String username;
    private final String commentId;
    private final String content;
    private int likes;
    final List<Comment> replies;

    public Comment(String username, String commentId, String content) {
        this.username = username;
        this.commentId = commentId;
        this.content = content;
        this.likes = 0;
        this.replies = new ArrayList<>();
    }

    Comparator<Comment> comparator = Comparator.comparing(Comment::getTotalLikes).reversed();

    public String getCommentId() {
        return commentId;
    }


    public void like() {
        likes++;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }

    public int getTotalLikes() {
        return likes + replies.stream().mapToInt(Comment::getTotalLikes).sum();
    }

    public String toString(int depth) {

        String intend = IntStream.range(0, depth).mapToObj(i -> " ").collect(Collectors.joining(""));
        return String.format("%sComment: %s\n%sWritten by: %s\n%sLikes: %d\n%s",
                intend, content, intend, username, intend, likes, replies.stream()
                        .sorted(comparator)
                        .map(comment -> comment.toString(depth + 4))
                        .collect(Collectors.joining("")));
    }
}

class Post {
    private final String username;
    private final String postContent;
    private final List<Comment> comments;

    public Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        this.comments = new ArrayList<>();
    }

    Comparator<Comment> comparator = Comparator.comparing(Comment::getTotalLikes).reversed();

    public void addComment(String username, String commentId, String content, String replyToId) {
        Comment reply = new Comment(username, commentId, content);
        if (replyToId == null) {
            comments.add(reply);
        } else {
            Comment replayedComment = findComment(replyToId, comments);
            replayedComment.addReply(reply);
        }
    }

    public void likeComment(String commentId) {
        Comment comment = findComment(commentId, comments);
        comment.like();
    }

    private Comment findComment(String commentId, List<Comment> comments) {

        return comments.stream().sorted(comparator.reversed())
                .filter(comment -> comment.getCommentId().equals(commentId))
                .findFirst()
                .orElseGet(() -> comments.stream().map(comment -> findComment(commentId, comment.replies))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null));
    }


    public String toString() {
        return String.format("Post: %s\nWritten by: %s\nComments:\n%s",
                postContent, username, comments.stream().sorted(comparator)
                        .map(comment -> comment.toString(8))
                        .collect(Collectors.joining("")));

    }

}


public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}
