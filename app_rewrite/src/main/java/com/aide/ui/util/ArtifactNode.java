package com.aide.ui.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Exclusion;

public class ArtifactNode extends BuildGradle.MavenDependency {

	public static String getClassifier(BuildGradle.MavenDependency dependency) {
		if (dependency instanceof ArtifactNode) {
			return ((ArtifactNode)dependency).classifier;
		}
		return null;
	}
	// 装箱
	public static ArtifactNode pack(BuildGradle.MavenDependency dep) {
		if (dep instanceof ArtifactNode) {
			return (ArtifactNode)dep;
		}
		return new ArtifactNode(dep, dep.version);
	}

	// 依赖排除
	private List<Exclusion> exclusions;
	public String classifier;

	public ArtifactNode(BuildGradle.MavenDependency dep, String version) {
		super(dep, version);

		if (dep instanceof ArtifactNode) {
			// 直接用字段，getExclusions可能返回emptyList
			ArtifactNode artifactNode = (ArtifactNode)dep;
			this.classifier = artifactNode.classifier;
			this.setExclusions(artifactNode.exclusions);
		}
		// 保留 packaging
		this.packaging = dep.packaging;
	}

	public ArtifactNode(int line) {
		super(line);
	}
	public ArtifactNode() {
		this(1);
	}
	public ArtifactNode(PomXml pom) {
		this();
		this.groupId = pom.group;
		this.artifactId = pom.artifact;
		this.version = pom.curVersion;
		// 从pom解析出来的
		this.packaging = pom.getPackaging();
	}

	@Override
	public String getGroupIdArtifactId() {
		String groupIdArtifactId = super.getGroupIdArtifactId();
		if (this.classifier != null) {
			groupIdArtifactId = groupIdArtifactId + ":" + this.classifier;
		}
		return groupIdArtifactId;
	}
	@Override
	public String toString() {
		String toString = super.toString();
		if (this.classifier != null) {
			toString += ":" + this.classifier;
		}
		return toString;
	}

	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		if( this.version != null ){
			hashCode += 17 * this.version.hashCode();
		}
		if (this.classifier != null) {
			hashCode += 17 * this.classifier.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof BuildGradle.MavenDependency) {
			BuildGradle.MavenDependency mavenDependency = (BuildGradle.MavenDependency)object;
			return getGroupIdArtifactId().equals(mavenDependency.getGroupIdArtifactId());
		} else {
			return super.equals(object);
		}
	}

	public void setExclusions(List<Exclusion> exclusions) {
		this.exclusions = exclusions;
	}

	public List<Exclusion> getExclusions() {
		if (this.exclusions == null) {
			return Collections.emptyList();
		}
		return this.exclusions;
	}

	public Set<String> getExclusionSet() {
		Set<String> exclusionSet = new HashSet<>();
		for (Exclusion exclusion : getExclusions()) {
			exclusionSet.add(exclusion.getGroupId() + ":" + exclusion.getArtifactId());
		}
		return exclusionSet;
	}

}
