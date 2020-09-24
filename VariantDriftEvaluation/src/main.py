import os

from src.plotting import create_generic_plot
from src.result_loading import load_results

data_directory = "./../../data/experimental_results"
save_dir = "./../../data/plots/"

print(os.listdir("."))


def main():
    setups = os.listdir(data_directory)
    for setup in setups:
        plot_stuff(source_dir=data_directory + "/" + setup, setup=setup)


def plot_stuff(source_dir: str, setup: str):
    all_methods = os.listdir(source_dir)
    results_per_method = load_results(all_methods, source_dir)

    all_datasets = ["ppu", "bcms", "Apogames"]

    methods_rand_known = [
        "NwM_ALL_MODELS_RANDOMLY_KNOWN_DISTRIBUTION",
        "PairwiseDesc_ALL_MODELS_RANDOMLY_KNOWN_DISTRIBUTION",
        "NameBased_ALL_MODELS_RANDOMLY_KNOWN_DISTRIBUTION"
    ]

    from pathlib import Path
    Path(save_dir).mkdir(parents=True, exist_ok=True)

    for dataset in all_datasets:
        if dataset == "ppu":
            dataset_name = "PPU"
        elif dataset == "bcms":
            dataset_name = "bCMS"
        elif dataset == "Apogames":
            dataset_name = "Apo-Games"
        else:
            dataset_name = dataset

        create_generic_plot(save_dir, methods_rand_known, dataset, results_per_method,
                            "NumberOfRefactorings", "Weight",
                            dataset_name + " " + setup, setup, use_legend=True, norm_y=False,
                            averaged=True, use_boundary=True)


if __name__ == "__main__":
    main()
