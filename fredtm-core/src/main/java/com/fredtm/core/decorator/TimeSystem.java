package com.fredtm.core.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fredtm.core.model.Collect;
import com.fredtm.core.model.TimeActivity;

import values.ActivityType;

public  abstract class TimeSystem  {

	private Collect collect;

	public TimeSystem(Collect collect) {
		this.collect = collect;
	}

	public List<TimeActivity> getTimes() {
		return collect.getTimes();
	}

	public Collect getCollect() {
		return this.collect;
	}

	public List<Double> convertAll() {
		return getTimes().stream().map(this::convertTime).collect(Collectors.toList());
	}

	public abstract Double convertTime(TimeActivity ta);

	public Double getValue(ActivityType type) {
		return getCollect().getTimes().stream().filter(ta -> ta.getActivity().getActivityType().equals(type))
				.mapToDouble(this::convertTime).sum();
	}

	public abstract String getSymbol();

	public LinkedHashMap<TimeActivity, Double> getValueMap() {
		List<TimeActivity> times = getTimes().stream()
				.sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())).collect(Collectors.toList());
		LinkedHashMap<TimeActivity, Double> collected = times.stream().collect(Collectors
				.toMap(Function.<TimeActivity> identity(), this::convertTime, (u, v) -> 0.0, LinkedHashMap::new));
		return collected;
	}

	public LinkedHashMap<TimeActivity, Double> getCumulativeValueMap() {
		LinkedHashMap<TimeActivity, Double> values = getValueMap();
		LinkedHashMap<TimeActivity, Double> newValues = new LinkedHashMap<>();

		BigDecimal sum = new BigDecimal(0);
		sum.setScale(2, RoundingMode.CEILING);
		Set<TimeActivity> times = values.keySet();
		for (TimeActivity t : times) {
			BigDecimal value = new BigDecimal(values.get(t));
			sum = sum.add(value);
			newValues.put(t, sum.doubleValue());
		}
		values = null;
		return newValues;
	}

	public Double getValueSimplified() {
		if (!getCollect().getTimes().isEmpty()) {
			return getCollect().getTimes().stream()
					.filter(ta -> !ta.getActivity().getActivityType().equals(ActivityType.PRODUCTIVE))
					.mapToDouble(this::convertTime).sum();
		} else {
			return 0.0;
		}
	}

	public LinkedHashMap<String, Optional<Double>> getTimeByActivities() {
		if (!getCollect().getTimes().isEmpty()) {
			return getCollect().getTimes().stream()
					.collect(
							Collectors.groupingBy(
									ta -> ta.getActivity().getTitle(),
									LinkedHashMap::new, 
									Collectors.mapping(this::convertTime,
													   Collectors.reducing((x, y) -> x + y))));
		} else {
			return null;
		}
	}
	


}
