vireo.model("WorkflowStep", function WorkflowStep(FieldProfile) {

	return function WorkflowStep() {

        this.instantiateFieldProfiles = function() {
            console.log("inst fp");
            console.log(this.aggregateFieldProfiles);
            var fieldProfiles = angular.copy(this.aggregateFieldProfiles);
            console.log(this.aggregateFieldProfiles);
            if (this.aggregateFieldProfiles) {
                this.aggregateFieldProfiles.length = 0;
            }
            console.log(this.aggregateFieldProfiles);
            fieldProfiles.forEach(function(fp) {
                console.log(this.aggregateFieldProfiles);
                fp = new FieldProfile(fp);
                fp.instantiateFieldValues();
                this.aggregateFieldProfiles.push(fp);
                console.log(fp);
            }, this);
        }

		return this;
	};

});
