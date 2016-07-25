/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas.affordance;

import org.springframework.hateoas.affordance.Suggestions.ExternalSuggestions;
import org.springframework.hateoas.affordance.Suggestions.RemoteSuggestions;
import org.springframework.hateoas.affordance.Suggestions.ValueSuggestions;

/**
 * @author Oliver Gierke
 */
public abstract class SuggestionHandlerSupport<T> implements SuggestionHandler<T> {

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.affordance.OptionsHandler#visit(org.springframework.hateoas.affordance.Options.ValueOptions)
	 */
	@Override
	public T visit(ValueSuggestions<?> options) {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.affordance.OptionsHandler#visit(org.springframework.hateoas.affordance.Options.ExternalOptions)
	 */
	@Override
	public T visit(ExternalSuggestions options) {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.affordance.OptionsHandler#visit(org.springframework.hateoas.affordance.Options.RemoteOptions)
	 */
	@Override
	public T visit(RemoteSuggestions options) {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.hateoas.affordance.OptionsHandler#visit(org.springframework.hateoas.affordance.Options)
	 */
	@Override
	public T visit(Suggestions options) {
		return null;
	}
}
