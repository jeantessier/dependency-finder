/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.metrics;

import java.util.*;

import org.apache.oro.text.perl.*;

public class MetricsFactory {
	private static final Perl5Util perl = new Perl5Util();

	private String default_project_name;
	
	private Map projects = new HashMap();
	private Map groups   = new HashMap();
	private Map classes  = new HashMap();
	private Map methods  = new HashMap();

	public MetricsFactory(String default_project_name) {
		this.default_project_name = default_project_name;
	}
	
	public Metrics CreateProjectMetrics() {
		return CreateProjectMetrics(default_project_name);
	}
	
	public Metrics CreateProjectMetrics(String name) {
		Metrics result = (Metrics) projects.get(name);

		if (result == null) {
			result = BuildProjectMetrics(name);
			projects.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildProjectMetrics(String name) {
		Metrics result = new Metrics(name);

		result.TrackMetric(new NbSubMetricsMeasurement(Metrics.GROUPS, result));
		result.TrackMetric(new AccumulatorMeasurement(Metrics.PACKAGES));
		result.TrackMetric(new StatisticalMeasurement("public classes per package", Metrics.PUBLIC_CLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("final classes per package", Metrics.FINAL_CLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("abstract classes per package", Metrics.ABSTRACT_CLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("interfaces per package", Metrics.INTERFACES, result));
		result.TrackMetric(new StatisticalMeasurement("inner classes per package", Metrics.INNER_CLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("static classes per package", Metrics.STATIC_CLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("classes per package", Metrics.CLASSES, result));
		result.TrackMetric(new StatisticalMeasurement(Metrics.NLOC, Metrics.NLOC, result, StatisticalMeasurement.DISPOSE_SUM));
		result.TrackMetric(new StatisticalMeasurement("subclasses per class", Metrics.SUBCLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("depth of inheritance per class", Metrics.DEPTH_OF_INHERITANCE, result));

		return result;
	}

	public Collection ProjectNames() {
		return Collections.unmodifiableCollection(projects.keySet());
	}
	
	public Collection ProjectMetrics() {
		return Collections.unmodifiableCollection(projects.values());
	}
	
	public Metrics CreateGroupMetrics(String name) {
		Metrics result = (Metrics) groups.get(name);

		if (result == null) {
			result = BuildGroupMetrics(name);
			groups.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildGroupMetrics(String name) {
		Metrics project_metrics = CreateProjectMetrics();
		Metrics result          = new Metrics(project_metrics, name);
		project_metrics.AddSubMetrics(result);

		result.TrackMetric(new StatisticalMeasurement(Metrics.NLOC, Metrics.NLOC, result, StatisticalMeasurement.DISPOSE_SUM));
		result.TrackMetric(new NbSubMetricsMeasurement(Metrics.CLASSES, result));
		result.TrackMetric(Metrics.PUBLIC_CLASSES);
		result.TrackMetric(Metrics.FINAL_CLASSES);
		result.TrackMetric(Metrics.ABSTRACT_CLASSES);
		result.TrackMetric(Metrics.SYNTHETIC_CLASSES);
		result.TrackMetric(Metrics.INTERFACES);
		result.TrackMetric(Metrics.INNER_CLASSES);
		result.TrackMetric(Metrics.DEPRECATED_CLASSES);
		result.TrackMetric(Metrics.STATIC_CLASSES);

		result.TrackMetric(new RatioMeasurement(Metrics.PUBLIC_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PUBLIC_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.FINAL_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.FINAL_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.ABSTRACT_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.ABSTRACT_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.SYNTHETIC_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.SYNTHETIC_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.INTERFACES_RATIO, (NumericalMeasurement) result.Metric(Metrics.INTERFACES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.DEPRECATED_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.DEPRECATED_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.STATIC_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.STATIC_CLASSES), (NumericalMeasurement) result.Metric(Metrics.CLASSES)));
		
		result.TrackMetric(new StatisticalMeasurement("methods per class", Metrics.METHODS, result));
		result.TrackMetric(new StatisticalMeasurement("attributes per class", Metrics.ATTRIBUTES, result));
		result.TrackMetric(new StatisticalMeasurement("subclasses per class", Metrics.SUBCLASSES, result));
		result.TrackMetric(new StatisticalMeasurement("depth of inheritance per class", Metrics.DEPTH_OF_INHERITANCE, result));

		return result;
	}

	public Collection GroupNames() {
		return Collections.unmodifiableCollection(groups.keySet());
	}

	public Collection GroupMetrics() {
		return Collections.unmodifiableCollection(groups.values());
	}

	public Metrics CreateClassMetrics(String name) {
		Metrics result = (Metrics) classes.get(name);

		if (result == null) {
			result = BuildClassMetrics(name);
			classes.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildClassMetrics(String name) {
		String package_name = "";
		int pos = name.lastIndexOf('.');
		if (pos != -1) {
			package_name = name.substring(0, pos);
		}
		Metrics package_metrics = CreateGroupMetrics(package_name);
		Metrics result          = new Metrics(package_metrics, name);
		package_metrics.AddSubMetrics(result);

		result.TrackMetric(new StatisticalMeasurement(Metrics.NLOC, Metrics.NLOC, result));

		result.TrackMetric(new NbSubMetricsMeasurement(Metrics.METHODS, result));
		result.TrackMetric(Metrics.PUBLIC_METHODS);
		result.TrackMetric(Metrics.PROTECTED_METHODS);
		result.TrackMetric(Metrics.PRIVATE_METHODS);
		result.TrackMetric(Metrics.PACKAGE_METHODS);
		result.TrackMetric(Metrics.FINAL_METHODS);
		result.TrackMetric(Metrics.ABSTRACT_METHODS);
		result.TrackMetric(Metrics.DEPRECATED_METHODS);
		result.TrackMetric(Metrics.SYNTHETIC_METHODS);
		result.TrackMetric(Metrics.STATIC_METHODS);
		result.TrackMetric(Metrics.SYNCHRONIZED_METHODS);
		result.TrackMetric(Metrics.NATIVE_METHODS);
		result.TrackMetric(Metrics.TRIVIAL_METHODS);

		result.TrackMetric(new RatioMeasurement(Metrics.PUBLIC_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.PUBLIC_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.PROTECTED_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.PROTECTED_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.PRIVATE_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.PRIVATE_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.PACKAGE_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.PACKAGE_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.FINAL_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.FINAL_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.ABSTRACT_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.ABSTRACT_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.DEPRECATED_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.DEPRECATED_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.SYNTHETIC_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.SYNTHETIC_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.STATIC_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.STATIC_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.SYNCHRONIZED_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.SYNCHRONIZED_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.NATIVE_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.NATIVE_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		result.TrackMetric(new RatioMeasurement(Metrics.TRIVIAL_METHODS_RATIO, (NumericalMeasurement) result.Metric(Metrics.TRIVIAL_METHODS), (NumericalMeasurement) result.Metric(Metrics.METHODS)));
		
		result.TrackMetric(Metrics.ATTRIBUTES);
		result.TrackMetric(Metrics.PUBLIC_ATTRIBUTES);
		result.TrackMetric(Metrics.PROTECTED_ATTRIBUTES);
		result.TrackMetric(Metrics.PRIVATE_ATTRIBUTES);
		result.TrackMetric(Metrics.PACKAGE_ATTRIBUTES);
		result.TrackMetric(Metrics.FINAL_ATTRIBUTES);
		result.TrackMetric(Metrics.DEPRECATED_ATTRIBUTES);
		result.TrackMetric(Metrics.SYNTHETIC_ATTRIBUTES);
		result.TrackMetric(Metrics.STATIC_ATTRIBUTES);
		result.TrackMetric(Metrics.TRANSIENT_ATTRIBUTES);
		result.TrackMetric(Metrics.VOLATILE_ATTRIBUTES);

		result.TrackMetric(new RatioMeasurement(Metrics.PUBLIC_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PUBLIC_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.PROTECTED_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PROTECTED_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.PRIVATE_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PRIVATE_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.PACKAGE_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PACKAGE_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.FINAL_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.FINAL_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.DEPRECATED_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.DEPRECATED_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.SYNTHETIC_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.SYNTHETIC_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.STATIC_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.STATIC_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.TRANSIENT_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.TRANSIENT_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		result.TrackMetric(new RatioMeasurement(Metrics.VOLATILE_ATTRIBUTES_RATIO, (NumericalMeasurement) result.Metric(Metrics.VOLATILE_ATTRIBUTES), (NumericalMeasurement) result.Metric(Metrics.ATTRIBUTES)));
		
		result.TrackMetric(Metrics.INNER_CLASSES);
		result.TrackMetric(Metrics.PUBLIC_INNER_CLASSES);
		result.TrackMetric(Metrics.PROTECTED_INNER_CLASSES);
		result.TrackMetric(Metrics.PRIVATE_INNER_CLASSES);
		result.TrackMetric(Metrics.PACKAGE_INNER_CLASSES);
		result.TrackMetric(Metrics.ABSTRACT_INNER_CLASSES);
		result.TrackMetric(Metrics.FINAL_INNER_CLASSES);
		result.TrackMetric(Metrics.STATIC_INNER_CLASSES);

		result.TrackMetric(new RatioMeasurement(Metrics.PUBLIC_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PUBLIC_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.PROTECTED_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PROTECTED_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.PRIVATE_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PRIVATE_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.PACKAGE_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.PACKAGE_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.ABSTRACT_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.ABSTRACT_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.FINAL_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.FINAL_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));
		result.TrackMetric(new RatioMeasurement(Metrics.STATIC_INNER_CLASSES_RATIO, (NumericalMeasurement) result.Metric(Metrics.STATIC_INNER_CLASSES), (NumericalMeasurement) result.Metric(Metrics.INNER_CLASSES)));

		result.TrackMetric(Metrics.SUBCLASSES);
		result.TrackMetric(Metrics.DEPTH_OF_INHERITANCE);
		result.TrackMetric(new AccumulatorMeasurement(Metrics.INBOUND_DEPENDENCIES));
		result.TrackMetric(new AccumulatorMeasurement(Metrics.OUTBOUND_DEPENDENCIES));
		result.TrackMetric(new StatisticalMeasurement("parameters per method", Metrics.PARAMETERS, result));
		result.TrackMetric(new StatisticalMeasurement("variables per method", Metrics.LOCAL_VARIABLES, result));

		return result;
	}

	public Collection ClassNames() {
		return Collections.unmodifiableCollection(classes.keySet());
	}

	public Collection ClassMetrics() {
		return Collections.unmodifiableCollection(classes.values());
	}
	
	public Metrics CreateMethodMetrics(String name) {
		Metrics result = (Metrics) methods.get(name);

		if (result == null) {
			result = BuildMethodMetrics(name);
			methods.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildMethodMetrics(String name) {
		String class_name = "";
		if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)$/", name)) {
			class_name = perl.group(1);
		} else if (perl.match("/^(.*)\\.\\{\\}$/", name)) {
			class_name = perl.group(1);
		}
		Metrics class_metrics = CreateClassMetrics(class_name);
		Metrics result        = new Metrics(class_metrics, name);
		class_metrics.AddSubMetrics(result);

		result.TrackMetric(Metrics.NLOC);
		result.TrackMetric(Metrics.PARAMETERS);
		result.TrackMetric(new AccumulatorMeasurement(Metrics.INBOUND_DEPENDENCIES));
		result.TrackMetric(new AccumulatorMeasurement(Metrics.OUTBOUND_DEPENDENCIES));
		result.TrackMetric(Metrics.LOCAL_VARIABLES);

		return result;
	}

	public Collection MethodNames() {
		return Collections.unmodifiableCollection(methods.keySet());
	}

	public Collection MethodMetrics() {
		return Collections.unmodifiableCollection(methods.values());
	}
}
