/*
 *  Dependency Finder - Comparing API differences between JAR files
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

package com.jeantessier.diff;

import com.jeantessier.classreader.*;

public abstract class FeatureDifferences extends DeprecatableDifferences {
	private Feature_info old_feature = null;
	private Feature_info new_feature = null;
	private boolean      inherited   = false;

	public FeatureDifferences(String name) {
		super(name);
	}

	public Feature_info OldFeature() {
		return old_feature;
	}

	protected void OldFeature(Feature_info old_feature) {
		this.old_feature = old_feature;
	}

	public Feature_info NewFeature() {
		return new_feature;
	}

	protected void NewFeature(Feature_info new_feature) {
		this.new_feature = new_feature;
	}

	public boolean Inherited() {
		return inherited;
	}

	public void Inherited(boolean inherited) {
		this.inherited = inherited;
	}

	public boolean Compare(Feature_info old_feature, Feature_info new_feature) {
		if (old_feature != null) {
			OldFeature(old_feature);
			OldDeclaration(old_feature.Declaration());

			if (new_feature != null) {
				NewFeature(new_feature);
				NewDeclaration(new_feature.Declaration());

				RemovedDeprecation(old_feature.IsDeprecated() && !new_feature.IsDeprecated());
				NewDeprecation(!old_feature.IsDeprecated() && new_feature.IsDeprecated());
			}
		} else if (new_feature != null) {
			NewFeature(new_feature);
			NewDeclaration(new_feature.Declaration());
		}

		return NewDeprecation() || RemovedDeprecation() || !IsEmpty();
	}
}
