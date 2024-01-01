package com.ampaiva.hostfully.repository;

import com.ampaiva.hostfully.model.Block;
import com.ampaiva.hostfully.model.Property;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BlockRepositoryTest {
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @ParameterizedTest
    @CsvSource({"11, 12, 0", "11, 13, 1", "13, 13, 1", "13, 19, 1", "13, 20, 1", "14, 19, 1", "14, 20, 1", "14, 21, 1", "20, 20, 1", "20, 21, 1", "21, 21, 0", "21, 22, 0"})
    void findBlocksByWhenCanceledIsFalse(String start, String end, int expected) {
        Property property = new Property();
        Property otherProperty = new Property();
        propertyRepository.saveAll(List.of(property, otherProperty));

        Block block = new Block();
        block.setProperty(property);
        block.setStart(LocalDate.parse("2024-01-13"));
        block.setEnd(LocalDate.parse("2024-01-20"));

        blockRepository.save(block);

        List<Block> result;
        result = blockRepository.findBlocksBy(property, LocalDate.parse("2024-01-" + start), LocalDate.parse("2024-01-" + end));
        assertEquals(expected, result.size());

        result = blockRepository.findBlocksBy(otherProperty, LocalDate.parse("2024-01-" + start), LocalDate.parse("2024-01-" + end));
        // if it is a different property doesn't cause conflicts
        assertEquals(0, result.size());
    }
}
