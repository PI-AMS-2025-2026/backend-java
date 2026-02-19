package com.fatec.horario.dtos;


public record ClassroomResponse(

  Long id,
  String name,
  String location,
  String physicalResources,
  String softwareResources,
  Integer capacity,
  Boolean template,
  Boolean practical

) {

    
}
