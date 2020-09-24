import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

from src.result_data import MethodStatistics, CombinedResult

legend_size = 13
title_size = 16
tick_size = 14
axis_label_size = 16

colors = ["blue", "red", 'orange', "purple", "green", ]
boundary = "BoundaryEstimation_ALL_MODELS_RANDOMLY_KNOWN_DISTRIBUTION"


def create_generic_plot(save_dir: str, methods: [], dataset, results_per_method: {}, x_parameter: str,
                        y_parameter: str, fig_title: str, setup: str, use_legend=False, norm_x=False, norm_y=False,
                        averaged=False, use_boundary=True):
    fig, ax = plt.subplots()
    ax.set_title(fig_title, fontsize=title_size)
    labels = []
    y_max = 0
    overall_x_min = 99999999
    overall_x_max = -1
    overall_y_min = 99999999
    overall_y_max = -1

    # Get the boundary results
    if use_boundary:
        boundary_statistics = results_per_method[boundary]
        boundary_on_dataset = boundary_statistics.get_results(dataset)

    # Load and normalize values
    method_to_x_vector = {}
    method_to_y_vector = {}
    for index, method in enumerate(methods):
        if method not in results_per_method:
            continue

        x_vector = []
        y_vector = []

        # Get the results of the current method
        method_statistics = results_per_method[method]  # type: MethodStatistics
        results_on_dataset = method_statistics.get_results(dataset)
        refactoring_counts = results_on_dataset.keys()
        refactoring_counts = sorted(refactoring_counts)
        for count in refactoring_counts:
            if use_boundary:
                boundary_result = boundary_on_dataset.get(count)
            combined_result = results_on_dataset.get(count)  # type: CombinedResult
            # Append the mean of x and y values to the lists
            if averaged:
                x_vector.append(combined_result.get_average(x_parameter))
                y_value = combined_result.get_average(y_parameter)
                if use_boundary:
                    y_value /= boundary_result.get_average(y_parameter)
                    y_value *= 100
                y_vector.append(y_value)
            else:
                x_vector.extend(combined_result.get_vector(x_parameter))
                y_values = combined_result.get_vector(y_parameter)
                if use_boundary:
                    boundary_values = boundary_result.get_vector(y_parameter)
                if use_boundary:
                    for i in range(len(y_values)):
                        y_values[i] = y_values[i] / boundary_values[i]
                        y_values[i] *= 100
                y_vector.extend(y_values)

        overall_x_min = min(overall_x_min, min(x_vector))
        overall_x_max = max(overall_x_max, max(x_vector))
        overall_y_min = min(overall_y_min, min(y_vector))
        overall_y_max = max(overall_y_max, max(y_vector))

        if method not in method_to_x_vector and method not in method_to_y_vector:
            method_to_x_vector[method] = x_vector
            method_to_y_vector[method] = y_vector
        else:
            raise RuntimeError()

    for index, method in enumerate(methods):
        if method not in results_per_method:
            continue
        name = get_real_name(method)

        x_vector = method_to_x_vector[method]
        y_vector = method_to_y_vector[method]

        if norm_x:
            x_vector = normalize(x_vector, min_val=overall_x_min, max_val=overall_x_max)
        if norm_y:
            y_vector = normalize(y_vector, min_val=overall_y_min, max_val=overall_y_max)

        y_min = 0
        if use_boundary:
            y_min = 5
        if norm_y:
            y_max = 1
        elif use_boundary:
            y_max = 10
        else:
            y_max = overall_y_max
        # Plot the results of the current method
        ax.scatter(x_vector, y_vector, color=colors[index], marker='x')
        if norm_x:
            x_name = x_parameter + " (normed)"
        elif x_parameter == "NumberOfRefactorings":
            x_name = "Number of Refactorings"
        else:
            x_name = x_parameter
        if norm_y:
            y_name = y_parameter + " (normed)"
        elif use_boundary:
            y_name = y_parameter + " [% of Upper Bound]"
        else:
            y_name = y_parameter
        plt.xlabel(x_name, fontsize=axis_label_size)
        plt.ylabel(y_name, fontsize=axis_label_size)
        labels.append(mpatches.Patch(color=colors[index], label=name))
        if use_legend:
            plt.legend(handles=labels, loc=2, prop={'size': legend_size})

    # Show the plot
    ax.tick_params(axis='x', labelsize=tick_size)
    ax.tick_params(axis='y', labelsize=tick_size)
    plt.ylim(y_min, y_max)
    plt.show()
    fig_save_name = fig_title.replace(" ", "_").replace(":", "_")
    fig_save_name += "_" + setup
    fig.savefig(save_dir + fig_save_name + ".pdf")


def get_real_name(old_name: str):
    parts = old_name.split("_")
    name = parts[0]
    if name == "PairwiseDesc":
        return "Pairwise"
    else:
        return name


def normalize(vector: [], min_val, max_val):
    norm = max_val - min_val
    vector_copy = []
    for value in vector:
        if norm != 0:
            new_value = (value - min_val) / norm
            if new_value < 0.00000001:
                new_value = 0
            vector_copy.append(new_value)
        else:
            vector_copy.append(value)
    return vector_copy
