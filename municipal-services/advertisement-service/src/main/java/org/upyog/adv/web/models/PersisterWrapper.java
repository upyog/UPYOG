package org.upyog.adv.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PersisterWrapper<T> {

	T advertisementDraftDetail;
}
