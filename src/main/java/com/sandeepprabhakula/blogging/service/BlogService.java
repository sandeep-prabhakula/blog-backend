package com.sandeepprabhakula.blogging.service;

import com.sandeepprabhakula.blogging.data.Blog;
import com.sandeepprabhakula.blogging.data.User;
import com.sandeepprabhakula.blogging.repository.BlogRepository;
import com.sandeepprabhakula.blogging.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    private final UserRepository userRepository;


    public List<Blog> getAllBlogs(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC,"_id"));
        Page<Blog> page = blogRepository.findAll(pageable);
        return page.getContent();
    }

    public Blog findBlogById(String id){
        Optional<Blog> findingBlog = blogRepository.findById(id);
        return findingBlog.orElse(null);
    }
    public String addNewBlog(Blog blog, String uid) {
        Optional<User> currentUser = userRepository.findById(uid);
        if(currentUser.isEmpty() || !currentUser.get().getRoles().equals("ROLE_ADMIN"))return "401 Unauthorized";
        blogRepository.save(blog);
        return "Blog Uploaded Successfully!";
    }


    public String updateBlog(Blog blog,String uid){
        Optional<User> currentUser = userRepository.findById(uid);
        if(currentUser.isEmpty() || !currentUser.get().getRoles().equals("ROLE_ADMIN"))return "401 Unauthorized";
        Optional<Blog> findingBlog = blogRepository.findById(blog.getId());
        if(findingBlog.isEmpty())return "Blog not found";
        Blog currentBlog = findingBlog.get();
        currentBlog.setTitle(blog.getTitle());
        currentBlog.setDescription(blog.getDescription());
        currentBlog.setImage(blog.getImage());
        currentBlog.setPostedAt(blog.getPostedAt());
        currentBlog.setCode(blog.getCode());
        blogRepository.save(currentBlog);
        return "Blog Updated";
    }

    public String deleteBlog(String blogId,String uid){
        Optional<User> currentUser = userRepository.findById(uid);
        if(currentUser.isEmpty() || !currentUser.get().getRoles().equals("ROLE_ADMIN"))return "401 Unauthorized";
        Optional<Blog> currentBlog = blogRepository.findById(blogId);
        if(currentBlog.isEmpty())return "Blog not found";
        blogRepository.deleteById(blogId);
        return "Blog deleted";
    }

}
