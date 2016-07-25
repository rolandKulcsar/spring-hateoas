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
public interface SimpleSuggestionHandler {

	void visit(ValueSuggestions<?> options);

	void visit(ExternalSuggestions options);

	void visit(RemoteSuggestions options);

	void visit(Suggestions options);
}
