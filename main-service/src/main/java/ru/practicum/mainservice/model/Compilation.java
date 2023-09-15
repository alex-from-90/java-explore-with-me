package ru.practicum.mainservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
public class Compilation {
    @Id
    @Column(name = "compilation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(
                    name = "compilation_id",
                    referencedColumnName = "compilation_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "event_id",
                    referencedColumnName = "event_id"
            )
    )
    private List<Event> events;

    @Column(name = "pinned", nullable = false)
    private boolean pinned;

    @Column(name = "title", length = 150)
    private String title;
}
