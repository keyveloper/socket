package org.example;

import lombok.Data;

@Data
public class CommentTypeMaker implements TypeMaker{
    private final String comment;
    private int typeNumber;
    @Override
    public CommentType make(){
        setTypeNumber(MessageTypeLibrary.COMMENT.ordinal());
        return new CommentType(comment, typeNumber);
    }
}
