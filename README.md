# SCOLAR — Systematic Component-Oriented Language Reuse

**SCOLAR** is a framework for systematic, component-oriented reuse of textual, external, translational Domain-Specific Languages (DSLs). It demonstrates how to package language fragments as reusable **language components** and arrange them into **language families** to enable configurable, technology-independent DSL derivation and customization.

---

## Overview

Modern software increasingly spans complex domains (manufacturing automation, robotics, aviation systems, software architecture) where domain experts must often collaborate on software artifacts. DSLs help domain experts express solutions in domain terms, but engineering DSLs is itself complex because it involves multiple artifacts (grammars, well-formedness rules, and code generators) and cross-layer integration.

SCOLAR aims to reduce this complexity by providing a top-down method for language reuse: encapsulate syntax and semantics of language fragments into language components, organize these components into language families (feature-based product lines), and compose or customize them to derive usable DSLs independent of any single language workbench.

---

## Key ideas & features

- **Language components**: Modular units that encapsulate a MontiCore grammar, well-formedness rule sets, and model-to-text (generator) transformations. Each component exposes an explicit interface with extension and provision points.
- **Language families**: Product-line-like groupings of components realized by a feature diagram. Selecting features configures which components and bindings are applied.
- **Bindings & composition**: Components are composed using bindings (grammar bindings, generator bindings, well-formedness rule bindings, and insertion bindings) that map provision points to extension points.
- **Technology independence**: The method is independently of a single workbench; the included tutorial demonstrates use with MontiCore.

---

## Requirements

- Java JDK 11 (or compatible JVM)
- Gradle (the project references Gradle 7.6)
---

## Getting started (tutorial)

1. Clone the repository:

```bash
git clone https://github.com/jerome-pfeiffer1/scolar.git
cd scolar
```

2. Build the projects `scolar-core` and `lwb-specific-implementations/scolar-monticore-instance` using gradle.

3. Explore the tutorial project and the comprised explanations.

---

## Project structure

- `scolar-core/` — core runtime and libraries for SCOLAR concepts (if present).
- `lwb-specific-implementations/` — technology-specific implementations (MontiCore adapters, wrappers, or templates).
- `tutorial/` — hands-on tutorial content and example language projects (primary entrypoint for users).
- `LICENSE` — BSD-3-Clause license.
- `README.md` — this document.

> Note: some folders and artifacts are examples; read individual subproject READMEs and `tutorial` notes for project-specific build details.

---

## Contributing

Contributions, bug reports, and pull requests are welcome. If you plan larger changes (new language workbench support, major refactor), please open an issue first so we can discuss design compatibility with the SCOLAR model.

When contributing:

- Follow existing code conventions and project structure.
- Add documentation for any added language component or binding type.
- Add tests or example models that show how to use new components.

---

## Citation
If you use SCOLAR in research or publications, please cite the relevant work by the authors:
- **Muctadir, H.M., Pfeiffer, J., Houdijk, J., Cleophas, L., Wortmann, A.** – [A Taxonomy of Change Types for Textual DSL Grammars](doi.org/10.5220/0013127800003896), 13th International Conference on Model-Based Software and Systems Engineering, 2025
- **Lehner, D., Pfeiffer, J., Klikovits, S., Wortmann, A., Wimmer, M.** – [A Method for Template-based Architecture Modeling and its Application to Digital Twins](http://dx.doi.org/10.5381/jot.2024.23.3.a8), Journal of Object Technology, 23(3), 2024
- **Pfeiffer, J., Lehner, D., Wortmann, A., Wimmer, M.** – [Towards a product line architecture for digital twins](https://doi.org/10.1109/ICSA-C57050.2023.00049), 2023 IEEE 20th International Conference on Software Architecture Companion (ICSA-C), 2023
- **Pfeiffer, J., Rumpe, B., Schmalzing, D., Wortmann, A.** – [Composition operators for modeling languages: A literature review](https://doi.org/10.1016/j.cola.2023.101226), Journal of Computer Languages, 76, 2023
- **Pfeiffer, J., Wortmann, A.** – [A low-code platform for systematic component-oriented language composition](https://doi.org/10.1145/3623476.3623516), Proceedings of the 16th ACM SIGPLAN International Conference on Software Language Engineering, 2023
- **Pfeiffer, J.** – [Systematic component-oriented language reuse](https://doi.org/10.1109/MODELS-C59198.2023.00043), 2023 ACM/IEEE International Conference on Model Driven Engineering Languages and Systems Companion (MODELS-C), 2023
- **Dalibor, M., Heithoff, M., Michael, J., Netz, L., Pfeiffer, J., Rumpe, B., Varga, S., Wortmann, A.** – [Generating customized low-code development platforms for digital twins](https://doi.org/10.1016/j.cola.2022.101117), Journal of Computer Languages, 70, 2022
- **Pfeiffer, J., Wortmann, A.** – [Towards the black-box aggregation of language components](https://doi.org/10.1109/MODELS-C53483.2021.00088), 2021 ACM/IEEE International Conference on Model Driven Engineering Languages and Systems Companion (MODELS-C), 2021
- **Butting, A., Pfeiffer, J., Rumpe, B., Wortmann, A.** – [A compositional framework for systematic modeling language reuse](https://doi.org/10.1145/3365438.3410934), Proceedings of the 23rd ACM/IEEE International Conference on Model Driven Engineering Languages and Systems (MODELS ’20), October 2020, Pages 14–25
 

---

## Contact & authorship

This project is maintained by the repository owner. For questions about the tutorial or publications that motivated SCOLAR, please refer to the repository owner profile on GitHub.

