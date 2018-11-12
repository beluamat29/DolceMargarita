package server;

import backend.entities.Pedido;
import backend.entities.chocolates.Chocolate;
import backend.entities.chocolates.TipoChocolate;
import backend.service.ServiceChocolate;
import org.uqbar.xtrest.api.Result;
import org.uqbar.xtrest.api.annotation.Body;
import org.uqbar.xtrest.api.annotation.Get;
import org.uqbar.xtrest.api.annotation.Post;
import org.uqbar.xtrest.http.ContentType;
import org.uqbar.xtrest.json.JSONUtils;
import org.eclipse.jetty.server.Request;
import org.uqbar.xtrest.result.ResultFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.uqbar.xtrest.result.ResultFactory.ok;

public class Server extends ResultFactory {

    private ServiceChocolate servicioDeChocolates;
    private JSONUtils JSONUtils;


    public Server(ServiceChocolate servicioDeChocolates) {
        this.JSONUtils = new JSONUtils();
        this.servicioDeChocolates = servicioDeChocolates;

    }

    @Get("/chocolates")
    public Result getChocolates(final String target, final Request baseRequest,
                                final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType(ContentType.APPLICATION_JSON);

        List<Chocolate> data = this.servicioDeChocolates.getChocolates();

        return ResultFactory.ok(this.JSONUtils.toJson(data));
    }

    @Get("/tiposChocolate")
    public Result getTiposChocolate(final String target, final Request baseRequest,
                                final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType(ContentType.APPLICATION_JSON);

        List<TipoChocolate> data = Arrays.asList(TipoChocolate.values());

        return ResultFactory.ok(this.JSONUtils.toJson(data));
    }

    @Post("/pedidos")
    public Result agregarPedido(@Body final String body, final String target, final Request baseRequest,
                                final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType(ContentType.APPLICATION_JSON);

        try {
            Integer dni = JSONUtils.getPropertyAsInteger(body,"dniCliente");
            Pedido nuevoPedido = new Pedido(dni);
            this.servicioDeChocolates.addPedido(nuevoPedido);
            return ResultFactory.ok();
        } catch (Exception e) {
            return ResultFactory.badRequest(e.getMessage());
        }

    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        {
            handleGet(target, baseRequest, request, response, "/chocolates");
        }
        {
            handleGet(target, baseRequest, request, response, "/tipoChocolate");
        }
        {
            handlePost(target, baseRequest, request, response, "/pedidos");
        }
    }

    private void handleGet(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String endPoint) {
        Matcher matcher =
                Pattern.compile(endPoint).matcher(target);

        if (request.getMethod().equalsIgnoreCase("Get") && matcher.matches()) {
            // take parameters from request

            // take variables from url

            // set default content type (it can be overridden during next call)
            response.setContentType("application/json");

            Result result = getChocolates(target, baseRequest, request, response);
            result.process(response);

            response.addHeader("Access-Control-Allow-Origin", "*");
            baseRequest.setHandled(true);
            return;
        }
    }

    private void handlePost(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String endPoint) {
        Matcher matcher =
                Pattern.compile(endPoint).matcher(target);

        if (request.getMethod().equalsIgnoreCase("Post") && matcher.matches()) {
            // take parameters from request
            String body = readBodyAsString(request);

            // take variables from url

            // set default content type (it can be overridden during next call)
            response.setContentType("application/json");

            Result result = agregarPedido(body, target, baseRequest, request, response);
            result.process(response);

            response.addHeader("Access-Control-Allow-Origin", "*");
            baseRequest.setHandled(true);
            return;
        }
    }
}