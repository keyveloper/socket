package org.example;

import lombok.Data;

@Data
public class CommentTypeMaker implements TypeMaker{
    private final String comment;
    @Override
    public CommentType make(){
        return new CommentType(comment);
    }
}
