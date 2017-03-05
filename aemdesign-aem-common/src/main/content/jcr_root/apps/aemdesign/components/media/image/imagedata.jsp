<%!

	final static String IMAGE_OPTION_FIXED_IMAGE_GENERATED = "fixedImageGenerated";

	final static String IMAGE_OPTION_FIXED_IMAGE_RENDITION = "fixedImageRendition";

	final static String IMAGE_OPTION_RESPONSIVE_GENERATED = "responsiveGenerated";

	final static String IMAGE_OPTION_RESPONSIVE_RENDITION = "responsiveRendition";

	final static String IMAGE_OPTION_RESPONSIVE_RENDITION_OVERRIDE = IMAGE_OPTION_RESPONSIVE_RENDITION + "Override";

	final static String DROP_TARGET_CSS_PREFIX_IMAGE = DropTarget.CSS_CLASS_PREFIX + "image";


	final static String RENDITION_PROFILE_CUSTOM = "custom";

	final static String RENDITION_REGEX_PATTERN = "^(\\w*)\\.(\\w*)\\.(\\d*)\\.(\\d*)\\.(\\S{3,4})$";

	/**
	 * Get the allowedDimension for the Image
	 * @param targetDimension
	 * @param _currentStyle
	 * @return
	 */
	public Integer getDimension(Integer targetDimension, Style _currentStyle){
		Integer dimension = null;
		if (targetDimension != null && targetDimension.intValue() > 0) {
			int max = _currentStyle.get(Image.PN_MAX_WIDTH,Integer.class);
			int min = _currentStyle.get(Image.PN_MIN_WIDTH,Integer.class);
			if (min <= targetDimension && targetDimension <= max){
				dimension = targetDimension;

			}else if (min > targetDimension){
//				image.set(Image.PN_HTML_WIDTH, String.valueOf(min));

				dimension = min;
			}else if (max < targetDimension){
//				image.set(Image.PN_HTML_WIDTH, String.valueOf(max));
				dimension = max;
			}
		}
		return dimension;
	}

	/**
	 * Get the nearest or equivalent width image set
	 * @param asset
	 * @param widthRenditionProfileMapping
	 * @param resolver
	 * @return
	 */
	public Map<Integer, String> filterFixedRenditionImageSet (com.adobe.granite.asset.api.Asset asset,  Map<Integer, String> widthRenditionProfileMapping, ResourceResolver resolver){

		Map<Integer, String> responsiveImageSet = new TreeMap<Integer, String>();

		Iterator renditionList = asset.listRenditions();

		Pattern pattern = Pattern.compile(RENDITION_REGEX_PATTERN);

		while(renditionList.hasNext()){

			com.adobe.granite.asset.api.Rendition rendition = (com.adobe.granite.asset.api.Rendition)renditionList.next();

			String renditonName = rendition.getName();

			renditonName = renditonName.trim();

			Matcher matcher = pattern.matcher(renditonName);

			boolean matches = matcher.matches();

			if (matches){

				String type = matcher.group(2);

				String w = matcher.group(3);

				int width = Integer.valueOf(w);

				if (RENDITION_PROFILE_CUSTOM.equals(type)){
					continue;
				}

				for (Integer minWidth : widthRenditionProfileMapping.keySet()){

					//Find the nearest or equivalent image
					if (minWidth.intValue() == width){
						responsiveImageSet.put(width, rendition.getPath());
						break;
					}else if (minWidth.intValue() < width){

						if (responsiveImageSet.isEmpty()){
							responsiveImageSet.put(width, rendition.getPath());
						}else {
							//Only first record
							Integer firstRecord = responsiveImageSet.keySet().iterator().next();

							if ( width < firstRecord ){
								responsiveImageSet.clear();;
								responsiveImageSet.put(width, rendition.getPath());
							}
						}
					}
				}
			}
		}
		return responsiveImageSet;
	}


	/**
	 * function to filter out the design dialog values which are not matching adaptive profile
	 * @param adaptiveImageWidthArray
	 * @param widthRenditionProfileMapping
	 * @param resolver
	 * @param path
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> filterAdaptiveImageSet (int [] adaptiveImageWidthArray,  Map<Integer, String> widthRenditionProfileMapping, ResourceResolver resolver, String path){

		Map<Integer, String> responsiveImageSet = new TreeMap<Integer, String>();

		int [] renditionList = adaptiveImageWidthArray;

		for (int rendition: renditionList){

			for (Integer minWidth : widthRenditionProfileMapping.keySet()){

				String profile = widthRenditionProfileMapping.get(minWidth);

				boolean isValidProfile = false;

				String profilePrefix = rendition + ".";

				if (profile.startsWith(profilePrefix)){

					MessageFormat mf = new MessageFormat(path);

					String url = mf.format(new Object[] {profile});

					url = resolver.map(url);
					responsiveImageSet.put(minWidth, url);
					isValidProfile = true;
				}

				if (isValidProfile == false){

					LOG.warn("Invalid profile : "+profile +" and profilePrefix : " + profilePrefix);

				}

			}

		}

		return responsiveImageSet;
	}


	/**
	 *function to filter out the design dialog values which are not matching rendition profile
	 * @param asset
	 * @param widthRenditionProfileMapping
	 * @param resolver
	 * @param isStandardType
	 * @return Map<Integer, String>
	 */
	public Map<Integer, String> filterRenditionImageSet (com.adobe.granite.asset.api.Asset asset,  Map<Integer, String> widthRenditionProfileMapping, ResourceResolver resolver, boolean isStandardType){

		Map<Integer, String> responsiveImageSet = new TreeMap<Integer, String>();

		if (asset != null) {

			Iterator renditionList = asset.listRenditions();

			Pattern pattern = Pattern.compile(RENDITION_REGEX_PATTERN);

			//System.out.println("Invalid matches : "+asset.listRenditions());

			while (renditionList.hasNext()) {

				com.adobe.granite.asset.api.Rendition rendition = (com.adobe.granite.asset.api.Rendition) renditionList.next();

				String renditonName = rendition.getName();

				renditonName = renditonName.trim();

				Matcher matcher = pattern.matcher(renditonName);

				boolean matches = matcher.matches();

				if (matches) {

					String type = matcher.group(2);
					String width = matcher.group(3);
					String height = matcher.group(4);

					//System.out.println("Invalid matches : "+matches +" renditonName : " + renditonName +" type:"+type);

					String renditionProfile = width;

					for (Integer minWidth : widthRenditionProfileMapping.keySet()) {

						//Skip the custom profile when it is requesting standard
						if (type.equals(RENDITION_PROFILE_CUSTOM) && isStandardType == true) {
							continue;
						}

						String profile = widthRenditionProfileMapping.get(minWidth);
						boolean isValidProfile = false;

						if (profile.equals(renditionProfile)) {
							String url = rendition.getPath();
							if (isStandardType == false) {
								url = url.replaceAll(type, RENDITION_PROFILE_CUSTOM);
							}
							url = resolver.map(url);
							responsiveImageSet.put(minWidth, url);
							isValidProfile = true;
						}

						if (isValidProfile == false) {
							LOG.warn("Invalid profile : [" + profile + "] renditionProfile : [" + renditionProfile + "] Asset Path : " + asset.getPath());
						}

					}
				}
			}
		}
		return responsiveImageSet;
	}

	/**
	 * Get the targetWith which is within the range from the Site
	 * @param style
	 * @param targetWidth
	 * @return
	 */
	public Map<Integer, String> getWidthProfileMap(Style style, int targetWidth){

		Map<Integer, String> widthRenditionProfileMap = new LinkedHashMap<Integer, String>();

		Integer maxWidth = style.get(ImageResource.PN_MAX_WIDTH, Integer.class);
		Integer minWidth = style.get(ImageResource.PN_MIN_WIDTH, Integer.class);

		targetWidth = Math.min(targetWidth, maxWidth);
		targetWidth = Math.max(targetWidth, minWidth);
		widthRenditionProfileMap.put(targetWidth, String.valueOf(targetWidth));

		return widthRenditionProfileMap;
	}


	/**
	 * Validate the List of the widthImageMapping from Design dialog and convert it into Map<Integer, String>
	 * @param widthImageMapping
	 * @return Map<Integer, String>
	 * @throws IllegalAccessException
	 */
	public Map<Integer, String> getWidthProfileMap(String [] widthImageMapping) throws IllegalAccessException{

		Map<Integer, String> widthRenditionProfileMap = new LinkedHashMap<Integer, String>();


		if (widthImageMapping != null && widthImageMapping.length > 0){

			for (String entry : widthImageMapping){
				String [] entryArray = StringUtils.split(entry, "=");
				if (entryArray == null || entryArray.length != 2){
					LOG.error("design widthImageMapping ["+entry+"] is invalid");
					new IllegalAccessException("design widthImageMapping ["+entry+"] is invalid");
				}
				String profile = StringUtils.split(entry, "=")[0];
				if (StringUtils.isEmpty(profile)){
					LOG.error("profile ["+profile+"] is invalid");
					new IllegalAccessException("profile ["+profile+"] is invalid");
				}
				String minWidth = StringUtils.split(entry, "=")[1];
				if (StringUtils.isEmpty(minWidth)  || (NumberUtils.isDigits(minWidth) == false)){
					LOG.error("minWidth ["+minWidth+"] is invalid");
					new IllegalAccessException("minWidth ["+minWidth+"] is invalid");
				}

				widthRenditionProfileMap.put(Integer.valueOf(minWidth), profile);

			}
		}

		return widthRenditionProfileMap;
	}

	/**
	 * Get the Adaptive Image Configuration for Supported Widths
	 * @param sling
	 * @return int []
	 * @throws IllegalAccessException
	 */
	public int []  getAdaptiveImageSupportedWidths(org.apache.sling.api.scripting.SlingScriptHelper sling) throws IllegalAccessException {

		int [] supportedWidths  = {320,767,1025,1365};

		org.osgi.service.cm.ConfigurationAdmin configAdmin = sling.getService(org.osgi.service.cm.ConfigurationAdmin.class);

		try{

			org.osgi.service.cm.Configuration config = configAdmin.getConfiguration("com.day.cq.wcm.foundation.impl.AdaptiveImageComponentServlet");


			Object obj = org.apache.sling.commons.osgi.PropertiesUtil.toStringArray(config.getProperties().get("adapt.supported.widths"));

			if (obj instanceof String []){

				String[] strings = (String [])obj;
				supportedWidths = new int[strings.length];
				for (int i=0; i < strings.length; i++) {
					supportedWidths[i] = Integer.parseInt(strings[i]);
				}
			}

			if (obj instanceof long []){

				long [] longs = (long [])obj;
				supportedWidths = new int[longs.length];
				for (int i=0; i < longs.length; i++) {
					supportedWidths[i] = (int)longs[i];
				}
			}


		}catch(Exception ex){
			LOG.error("failed to retrieve OSGI configuration : "+ex.getMessage(), ex);
		}

		return supportedWidths;
	}


%>