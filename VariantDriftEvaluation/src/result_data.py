import numpy


class SingleResult:
    def __init__(self, json_content):
        self.run_id = json_content["runID"]
        self.method = json_content["method"]
        self.dataset = json_content["dataset"]
        self.runtime = json_content["runtime"]
        self.weight = json_content["weight"]
        self.number_of_models = json_content["numberOfModels"]
        self.number_of_elements = json_content["numberOfElements"]
        self.number_of_tuples = json_content["numberOfTuples"]
        self.size_of_largest_model = json_content["sizeOfLargestModel"]
        self.policy = json_content["policy"]
        self.use_known_distribution = json_content["useKnownRefactoringDistribution"]
        self.number_of_refactorings = json_content["numberOfRefactorings"]
        self.number_of_rename_property = json_content["numberOfRenameProperty"]
        self.number_of_rename_element = json_content["numberOfRenameElement"]
        self.number_of_move_property = json_content["numberOfMoveProperty"]
        self.number_of_extract_interface_copy = json_content["numberOfExtractInterfaceCopy"]
        self.number_of_extract_interface_move = json_content["numberOfExtractInterfaceMove"]


class CombinedResult:
    def __init__(self, l_single_results: list, result_file: str):
        self.l_single_results = l_single_results
        # Make sure there is at least one result in the list
        if self.l_single_results is None:
            raise ValueError("The list of results is None!")
        elif len(self.l_single_results) < 1:
            raise ValueError("No results provided!")

        # Initialize all data that has to be the same across all results
        first_result = self.l_single_results[0]  # type: SingleResult
        self.run_id = first_result.run_id
        self.method = first_result.method
        self.number_of_models = first_result.number_of_models
        self.number_of_elements = first_result.number_of_elements
        self.size_of_largest_model = first_result.size_of_largest_model
        self.policy = first_result.policy
        self.use_known_distribution = first_result.use_known_distribution

        name_parts = first_result.dataset.split("_")
        self.dataset = ""
        for part in name_parts[:-3]:
            self.dataset += part + "_"
        self.dataset = self.dataset[:-1]

        set_refactorings = first_result.dataset.split("_")
        self.expected_number_of_refactorings = int(set_refactorings[-2])

        self.name = self.method + "_" + self.policy
        if self.use_known_distribution:
            self.name = self.name + "_" + "KNOWN_DISTRIBUTION"
        else:
            self.name = self.name + "_" + "UNKNOWN_DISTRIBUTION"

        # Make sure that the shared data above is actually the same
        for single_result in self.l_single_results:  # type: SingleResult
            if not (self.run_id == single_result.run_id
                    and self.method == single_result.method
                    and self.number_of_models == single_result.number_of_models
                    and self.policy == single_result.policy
                    and self.use_known_distribution == single_result.use_known_distribution):
                raise ValueError("The given results for " + self.method + " and " + single_result.method +
                                 " are not from the same setup and should not be averaged!")

        # Lists to collect the data of individual runs
        self.lst_runtime = []
        self.lst_number_of_tuples = []
        self.lst_weight = []
        self.lst_number_of_refactorings = []
        self.lst_number_of_rename_property = []
        self.lst_number_of_rename_element = []
        self.lst_number_of_move_property = []
        self.lst_number_of_extract_interface_copy = []
        self.lst_number_of_extract_interface_move = []

        for single_result in self.l_single_results:
            self.lst_runtime.append(single_result.runtime)
            self.lst_number_of_tuples.append(single_result.number_of_tuples)
            self.lst_weight.append(single_result.weight)
            self.lst_number_of_refactorings.append(single_result.number_of_refactorings)
            self.lst_number_of_rename_property.append(single_result.number_of_rename_property)
            self.lst_number_of_rename_element.append(single_result.number_of_rename_element)
            self.lst_number_of_move_property.append(single_result.number_of_move_property)
            self.lst_number_of_extract_interface_copy.append(single_result.number_of_extract_interface_copy)
            self.lst_number_of_extract_interface_move.append(single_result.number_of_extract_interface_move)

    def get_vector(self, variable_name: str):
        if variable_name == "Weight":
            return self.lst_weight
        elif variable_name == "NumberOfRefactorings":
            return self.lst_number_of_refactorings
        elif variable_name == "NumberOfRenameProperty":
            return self.lst_number_of_rename_property
        elif variable_name == "NumberOfRenameElement":
            return self.lst_number_of_rename_element
        elif variable_name == "NumberOfMoveProperty":
            return self.lst_number_of_move_property
        elif variable_name == "NumberOfExtractInterfaceCopy":
            return self.lst_number_of_extract_interface_copy
        elif variable_name == "NumberOfExtractInterfaceMove":
            return self.lst_number_of_extract_interface_move
        elif variable_name == "ExpectedNumOfRefactorings":
            return self.expected_number_of_refactorings

    def get_average(self, variable_name: str):
        if variable_name == "Weight":
            return numpy.average(self.lst_weight)
        elif variable_name == "NumberOfRefactorings":
            return numpy.average(self.lst_number_of_refactorings)
        elif variable_name == "NumberOfRenameProperty":
            return numpy.average(self.lst_number_of_rename_property)
        elif variable_name == "NumberOfRenameElement":
            return numpy.average(self.lst_number_of_rename_element)
        elif variable_name == "NumberOfMoveProperty":
            return numpy.average(self.lst_number_of_move_property)
        elif variable_name == "NumberOfExtractInterfaceCopy":
            return numpy.average(self.lst_number_of_extract_interface_copy)
        elif variable_name == "NumberOfExtractInterfaceMove":
            return numpy.average(self.lst_number_of_extract_interface_move)
        elif variable_name == "ExpectedNumOfRefactorings":
            return self.expected_number_of_refactorings

    def get_maximum(self, variable_name: str):
        if variable_name == "Weight":
            return numpy.max(self.lst_weight)
        elif variable_name == "NumberOfRefactorings":
            return numpy.max(self.lst_number_of_refactorings)
        elif variable_name == "NumberOfRenameProperty":
            return numpy.max(self.lst_number_of_rename_property)
        elif variable_name == "NumberOfRenameElement":
            return numpy.max(self.lst_number_of_rename_element)
        elif variable_name == "NumberOfMoveProperty":
            return numpy.max(self.lst_number_of_move_property)
        elif variable_name == "NumberOfExtractInterfaceCopy":
            return numpy.max(self.lst_number_of_extract_interface_copy)
        elif variable_name == "NumberOfExtractInterfaceMove":
            return numpy.max(self.lst_number_of_extract_interface_move)
        elif variable_name == "ExpectedNumOfRefactorings":
            return self.expected_number_of_refactorings

    def get_minimum(self, variable_name: str):
        if variable_name == "Weight":
            return numpy.min(self.lst_weight)
        elif variable_name == "NumberOfRefactorings":
            return numpy.min(self.lst_number_of_refactorings)
        elif variable_name == "NumberOfRenameProperty":
            return numpy.min(self.lst_number_of_rename_property)
        elif variable_name == "NumberOfRenameElement":
            return numpy.min(self.lst_number_of_rename_element)
        elif variable_name == "NumberOfMoveProperty":
            return numpy.min(self.lst_number_of_move_property)
        elif variable_name == "NumberOfExtractInterfaceCopy":
            return numpy.min(self.lst_number_of_extract_interface_copy)
        elif variable_name == "NumberOfExtractInterfaceMove":
            return numpy.min(self.lst_number_of_extract_interface_move)
        elif variable_name == "ExpectedNumOfRefactorings":
            return self.expected_number_of_refactorings


class MethodStatistics:
    def __init__(self, method_name: str):
        self.method_name = method_name
        self.result_per_dataset = {}

    def add_result(self, result: CombinedResult):
        if result.dataset in self.result_per_dataset:
            # Get the dictionary of results per run_id: e.g: 2 -> AverageResult
            result_per_num_of_refs = self.result_per_dataset.get(result.dataset)  # type: dict
        else:
            result_per_num_of_refs = dict()
            self.result_per_dataset[result.dataset] = result_per_num_of_refs
        if result.expected_number_of_refactorings in result_per_num_of_refs:
            raise ValueError("There already is a run for " + str(result.expected_number_of_refactorings)
                             + " refactorings for " + result.dataset +
                             " saved for " + self.method_name + "!")
        result_per_num_of_refs[result.expected_number_of_refactorings] = result

    def get_results(self, dataset: str) -> dict:
        return self.result_per_dataset[dataset]

    def has_result(self, dataset: str) -> bool:
        return dataset in self.result_per_dataset
