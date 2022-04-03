package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.ResultResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private ModelMapper mapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        Post post = this.mapToEntity(postDto);
        Post savedPost = postRepository.save(post);
        return this.mapToDto(savedPost);
    }

    @Override
    public ResultResponse<PostDto> getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sort);

        Page<Post> pageablePosts = postRepository.findAll(pageRequest);

        List<PostDto> content = pageablePosts.getContent().stream().map(this::mapToDto).collect(Collectors.toList());

        ResultResponse<PostDto> resultResponse = new ResultResponse<>();
        resultResponse.setContent(content);
        resultResponse.setPageNo(pageablePosts.getNumber());
        resultResponse.setPageSize(pageablePosts.getSize());
        resultResponse.setTotalElement(pageablePosts.getTotalElements());
        resultResponse.setTotalPages(pageablePosts.getTotalPages());
        resultResponse.setLast(pageablePosts.isLast());

        return resultResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Post", "id", id));
        return this.mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = this.postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        Post updatedPost = this.postRepository.save(post);
        return this.mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        this.postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        this.postRepository.deleteById(id);
    }

    private PostDto mapToDto(Post post){
        PostDto mappedPostDto = this.mapper.map(post, PostDto.class);
        return mappedPostDto;
    }

    private Post mapToEntity(PostDto postDto){
        Post post = this.mapper.map(postDto, Post.class);
        return post;
    }
}
