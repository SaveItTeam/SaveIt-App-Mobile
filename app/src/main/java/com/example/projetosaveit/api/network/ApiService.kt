package com.example.projetosaveit.api.network

import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.EmpresaInsertDTO
import com.example.projetosaveit.model.FuncionarioInsertDTO
import com.example.projetosaveit.model.LoteInsertDTO
import com.example.projetosaveit.model.VitrineDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/batch/listarProdutosLote/{enterpriseId}")
    fun getProdutos(@Path("enterpriseId") idEmpresa : Long): Call<List<Produto>>

    @GET("api/image/showcase-images")
    fun getVitrine(): Call<List<VitrineDTO>>

    @POST("api/batch/inserir")
    fun postLote(@Body produto: LoteInsertDTO): Call<ResponseBody>

    @GET("api/enterprise/listarEmail/{email}")
    fun getEmpresaEmail(@Path("email") email : String): Call<EmpresaDTO>

    @POST("api/employee/inserir")
    fun postFuncionario(@Body funcionario: FuncionarioInsertDTO): Call<ResponseBody>

    @POST("api/enterprise/inserir")
    fun postEmpresa(@Body empresa : EmpresaInsertDTO) : Call<ResponseBody>
}