package com.connect.hub.blog.service;

import com.connect.hub.blog.model.Blog;
import com.connect.hub.blog.model.BlogDTO;
import com.connect.hub.blog.model.Tag;
import com.connect.hub.blog.repository.BlogRepository;
import com.connect.hub.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private TagRepository tagRepository;

    public void createBlog(BlogDTO blog, String emailId, MultipartFile file) throws IOException {

        Blog blogData = Blog.builder()
                .body(blog.body)
                .emailId(emailId)
                .file(file.getBytes())
                .likes(0)
                .title(blog.title)
                .build();
        Tag tag = new Tag();
        tag.setName(blog.tag);
        tag.setBlog(blogData);
        blogData.setTag(List.of(tag));
        blogRepository.save(blogData);
    }

    public List<Blog> getBlogs() {
        return blogRepository.findAll();
    }
    public List<Blog> getBlogs(String tag) {
        List<Tag> tagList = tagRepository.findByName(tag);
        List<Blog> blogList = new ArrayList<>();
        for (Tag tags : tagList){
            blogList.add(tags.getBlog());
        }
        return blogList;
    }

    public ResponseEntity<?> editBlog(MultipartFile file, String title, String body, Long id, String emailId) throws IOException {
        Optional<Blog> optional = blogRepository.findById(id);
        if(optional.isPresent()){
            Blog blog = optional.get();
            if (Objects.equals(emailId, blog.getEmailId())){
                blog.setBody(body);
                blog.setFile(file.getBytes());
                blog.setTitle(title);
                blogRepository.save(blog);
            }
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> deleteBlog(Long id, String emailId) {
        Optional<Blog> optional = blogRepository.findById(id);
        if(optional.isPresent()){
            blogRepository.delete(optional.get());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}