package com.example.projetosaveit.model

import java.util.Date

class VitrineInsertDTO(
    var description : String,
    var batchId : Long,
    var quantityShowcase : Int,
    var entranceDate : Date
) {
}