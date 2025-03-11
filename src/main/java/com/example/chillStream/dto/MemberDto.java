package com.example.chillStream.dto;

import com.example.chillStream.constant.Role;
import com.example.chillStream.constant.Subscription;
import com.example.chillStream.entity.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class MemberDto {
    private Long id;
    private String name;
    private String email;
    private String tel;
    private Role role;
    private Subscription subscription;

    public static ModelMapper modelMapper = new ModelMapper();

    public static MemberDto of(Member member){
        return modelMapper.map(member, MemberDto.class);
    }
}
