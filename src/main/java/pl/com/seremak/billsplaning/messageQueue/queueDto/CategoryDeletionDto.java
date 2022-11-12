package pl.com.seremak.billsplaning.messageQueue.queueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CategoryDeletionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private String deletedCategory;
    private String replacementCategory;
}
