package programmingtheiot.part03.integration.connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.connection.handlers.UpdateSystemPerformanceResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateTelemetryResourceHandler;

import org.eclipse.californium.core.server.resources.Resource;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import programmingtheiot.data.DataUtil;

public class UpdateResourceHandlerTest {

	private CoapServer server = null;

	@Before
	public void setUp() throws Exception {
		this.server = new CoapServer(ConfigConst.DEFAULT_COAP_PORT);

		// Añadir handlers de recursos simulados para Telemetría y Rendimiento del Sistema
		Resource telemetryResource = new UpdateTelemetryResourceHandler(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE);
		Resource sysPerfResource = new UpdateSystemPerformanceResourceHandler(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE);

		this.server.add(telemetryResource);
		this.server.add(sysPerfResource);

		this.server.start();

		Thread.sleep(1000); // Dar tiempo al servidor para iniciar completamente
	}

	@After
	public void tearDown() throws Exception {
		if (this.server != null) {
			this.server.stop();
			this.server.destroy();
		}
	}

	@Test
	public void testTelemetryPutMessage() {
		CoapClient client = new CoapClient("coap://localhost:" + ConfigConst.DEFAULT_COAP_PORT +
			"/" + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());

		SensorData sd = new SensorData();
		sd.setName("Temp");
		sd.setValue(23.5f);

		String payload = DataUtil.getInstance().sensorDataToJson(sd);

		String response = client.put(payload, MediaTypeRegistry.APPLICATION_JSON).getResponseText();

		assertNotNull("No se recibió respuesta del PUT de SensorData", response);
		assertTrue("El PUT de SensorData (telemetría) falló", response.contains("Telemetry resource updated"));
	}

	@Test
	public void testSystemPerformancePutMessage() {
		CoapClient client = new CoapClient("coap://localhost:" + ConfigConst.DEFAULT_COAP_PORT +
			"/" + ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());

		SystemPerformanceData spd = new SystemPerformanceData();
		spd.setCpuUtilization(0.5f);
		spd.setMemoryUtilization(0.7f);
		spd.setDiskUtilization(0.9f);

		String payload = DataUtil.getInstance().systemPerformanceDataToJson(spd);

		String response = client.put(payload, MediaTypeRegistry.APPLICATION_JSON).getResponseText();

		assertNotNull("No se recibió respuesta del PUT de SystemPerformanceData", response);
		assertTrue("El PUT de SystemPerformanceData falló", response.contains("System performance resource updated"));
	}
}
