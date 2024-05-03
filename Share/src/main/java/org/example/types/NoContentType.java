package org.example.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NoContentType implements MessageType {
    private final String role;
    // 이 클래스는 그냥 없애도 되는거 아님?
}
