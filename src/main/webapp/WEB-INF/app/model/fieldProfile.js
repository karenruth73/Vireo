vireo.model("FieldProfile", function FieldProfile(FieldValue, WsApi) {

	return function FieldProfile() {

        this.instantiateFieldValues = function() {
            console.log("inst fv");
            console.log(this)
            var fieldValues = angular.copy(this.fieldValues);
            if (this.fieldValues) {
                this.fieldValues.length = 0;
            }
            angular.forEach(fieldValues, function(fv) {
                fv = new FieldValue(fv);
                console.log(fv);
                this.fieldValues.push(fv);
            }, this);
        }

        angular.extend(apiMapping.FieldProfile.fieldValuesListen, {
            'method': this.id + '/field-values'
        });
        WsApi.listen(apiMapping.FieldProfile.fieldValuesListen).then(null, null, function(data) {
            var replacedFieldValue = false;
            var newFieldValue = angular.fromJson(data.body).payload.FieldValue;
            var emptyFieldValues = [];
            var fieldValue;
            for (var i in this.fieldValues) {
                fieldValue = this.fieldValues[i];
                if (fieldValue.fieldPredicate.id === newFieldValue.fieldPredicate.id) {
                    if (fieldValue.id) {
                        if (fieldValue.id === newFieldValue.id) {
                            angular.extend(fieldValue, newFieldValue);
                            replacedFieldValue = true;
                            break;
                        }
                    } else {
                        emptyFieldValues.push(fieldValue);
                    }
                }
            }
            if (emptyFieldValues.length === 1) {
                fieldValue = emptyFieldValues[0];
                angular.extend(fieldValue, newFieldValue);
                replacedFieldValue = true;
            }
            if (!replacedFieldValue) {
                fieldValue = new FieldValue(newFieldValue);
                this.fieldValues.push(fieldValue);
            }

            if (fieldValue.fieldPredicate.documentTypePredicate) {
                enrichDocumentTypeFieldValue(fieldValue);
            }
        });

        this.saveFieldValue = function(fieldValue) {
            console.log(fieldValue);
            fieldValue.setIsValid(true);
            fieldValue.setValidationMessages([]);

            if ((!fieldValue.value || fieldValue.value === "") && !this.optional) {
                return $q(function(resolve) {
                    fieldValue.setIsValid(false);
                    fieldValue.addValidationMessage("This field is required");
                    resolve();
                });
            }

            console.log(this.getMapping());
            angular.extend(this.getMapping().saveFieldValue, {
                method: this.id + "/update-field-value/",
                data: fieldValue
            });

            var promise = WsApi.fetch(this.getMapping().saveFieldValue);

            promise.then(function(response) {
                var responseObj = angular.fromJson(response.body);
                if (responseObj.meta.type === "INVALID") {
                    fieldValue.setIsValid(false);
                    angular.forEach(responseObj.payload.HashMap.value, function(value) {
                        fieldValue.addValidationMessage(value);
                    });

                } else {
                    fieldValue.setIsValid(true);
                    var updatedFieldValue = responseObj.payload.FieldValue;
                    var matchingFieldValues = {};
                    for (var i = this.fieldValues.length - 1; i >= 0; i--) {
                        var currentFieldValue = this.fieldValues[i];
                        if ((currentFieldValue.id === undefined || currentFieldValue.id === updatedFieldValue.id) && currentFieldValue.value == updatedFieldValue.value && currentFieldValue.fieldPredicate.id == updatedFieldValue.fieldPredicate.id) {

                            matchingFieldValues[i] = currentFieldValue;

                            for (var j in matchingFieldValues) {
                                if (currentFieldValue.file !== undefined) {
                                    matchingFieldValues[j].file = currentFieldValue.file;
                                }
                            }
                        }
                    }
                    var updated = false;
                    for (var k in matchingFieldValues) {
                        if (!updated) {
                            updated = true;
                            angular.extend(matchingFieldValues[k], updatedFieldValue);
                        } else {
                            this.fieldValues.splice(k, 1);
                        }
                    }
                }

            });

            return promise;
        };

		return this;
	};

});
