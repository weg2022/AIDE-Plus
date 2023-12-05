package io.github.zeroaicy.aide;


import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import io.github.zeroaicy.aide.AllowedListContentProvider.Provider;

public class AllowedListContentProvider{

	Set<Provider> providers = new HashSet<>();
	public AllowedListContentProvider addProvider(AllowedListContentProvider.Provider provider){
		if ( provider == null ) return this;
		providers.add(provider);
		return this;
	}

	public interface Provider{

		public boolean hasDefaultMethod(String defaultMethodSign);

		public void addDefaultMethod(String defaultMethodSign);
	}

	private static AllowedListContentProvider allowedListContentProvider = new AllowedListContentProvider();

	public static AllowedListContentProvider get(){
		return allowedListContentProvider;
	}

	public boolean hasDefaultMethod(String defaultMethodSign){

		Iterator<AllowedListContentProvider.Provider> iterator = providers.iterator();
		while ( iterator.hasNext() ){
			AllowedListContentProvider.Provider provider = iterator.next();
			return provider.hasDefaultMethod(defaultMethodSign);
		}
		return false;
	}
	public void addDefaultMethod(String defaultMethodSign){
		Iterator<AllowedListContentProvider.Provider> iterator = providers.iterator();
		while ( iterator.hasNext() ){
			AllowedListContentProvider.Provider provider = iterator.next();
			provider.addDefaultMethod(defaultMethodSign);
		}
	}
}
