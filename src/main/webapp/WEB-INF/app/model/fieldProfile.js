vireo.model("FieldProfile", function FieldProfile(FieldValue) {

	return function FieldProfile() {

		this.before(function() {
            instantiateFieldValues();
        });

        var instantiateFieldValues = function() {
            var fieldValues = angular.copy(this.fieldValues);
            if (this.fieldValues) {
                this.fieldValues.length = 0;
            }
            angular.forEach(fieldValues, function(fv) {
                fv = new FieldValue(fv);
                this.fieldValues.push(fv);
            });
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

		return this;
	};

});
