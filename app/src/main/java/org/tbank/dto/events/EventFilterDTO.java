package org.tbank.dto.events;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDTO {
    private String name;
    private Long placeId;
    private LocalDate fromDate;
    private LocalDate toDate;
}
