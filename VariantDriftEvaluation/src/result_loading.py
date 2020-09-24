import glob
import json

from src.result_data import SingleResult, CombinedResult, MethodStatistics


def load_result_file(path_to_file: str):
    with open(path_to_file, "r") as file:
        file_content = file.readlines()
    l_single_results = []
    for line in file_content:
        # Load each line separately to parse it
        json_content = json.loads(line)
        l_single_results.append(SingleResult(json_content))

    return l_single_results


def load_results(methods, result_dir):
    results_per_method = {}
    for method in methods:
        stat_directory = result_dir + "/" + method
        stat_files = glob.glob(stat_directory + "/*_stats.json")

        for stat_file in stat_files:
            combined_result = CombinedResult(load_result_file(stat_file), stat_file)

            if combined_result.method != method:
                raise AssertionError("Names do not match: " + combined_result.method + " - - " + method)

            method_name = combined_result.name

            if method_name in results_per_method:
                method_statistics = results_per_method[method_name]
            else:
                method_statistics = MethodStatistics(method_name)
                results_per_method[method_name] = method_statistics

            method_statistics.add_result(combined_result)

    return results_per_method


def group_results_by_run_id(l_single_results: list):
    result_per_run_id = dict()
    for single_result in l_single_results:  # type: SingleResult
        run_id = single_result.run_id
        if run_id in result_per_run_id:
            grouped_results = result_per_run_id[run_id]
        else:
            grouped_results = []
            result_per_run_id[run_id] = grouped_results
        grouped_results.append(single_result)
    return result_per_run_id
