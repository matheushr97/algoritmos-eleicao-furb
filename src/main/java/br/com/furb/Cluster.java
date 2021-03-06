package br.com.furb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.furb.model.SystemProcess;

public class Cluster {

	private static final Logger log = LoggerFactory.getLogger(Cluster.class);

	private static Cluster instance;

	private List<SystemProcess> processes;

	private Optional<SystemProcess> coordinator;

	private Cluster() {
		this.processes = new ArrayList<>();
		this.coordinator = Optional.empty();
	}

	public static Cluster getInstance() {
		return instance == null ? instance = new Cluster() : instance;
	}

	public void createNewProcess() {
		SystemProcess process = generateNewProcess();
		log.info("Criando novo processo " + process.toString());
		this.processes.add(process);
	}

	public void desactivateProcess() {
		int lastIndex = processes.size() - 1;
		SystemProcess randomProcess = processes.get(new Random().nextInt(lastIndex));
		this.processes.remove(randomProcess);
		log.info(String.format("Processo %s desativado", randomProcess.toString()));
	}

	public void checkCoordinator() {
		if (!processes.isEmpty()) {
			int randomIndex = new Random().nextInt(processes.size());
			SystemProcess randomProcess = processes.get(randomIndex);
			randomProcess.requestToCoordinator();
		}
	}

	public void desactivateCoordinator() {
		if (coordinator.isPresent()) {
			this.processes.remove(coordinator.get());
			log.info(String.format("Coordenador %s foi inativado", coordinator.get()));
		}
		this.coordinator = Optional.empty();
	}

	private SystemProcess generateNewProcess() {
		SystemProcess process = new SystemProcess(new Random().nextInt(100));
		boolean exists = processes.stream().filter(p -> p.equals(process)).findAny().isPresent();
		if (exists)
			return generateNewProcess();

		return process;
	}

	public Optional<SystemProcess> getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(Optional<SystemProcess> coordinator) {
		if (coordinator.isPresent()) {
			log.info(String.format("Processo %s foi eleito o novo coordenador", coordinator.get().toString()));
		}

		this.coordinator = coordinator;
	}

	public List<SystemProcess> getProcesses() {
		return processes;
	}

}
